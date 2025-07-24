package com.henrypeya.data.repository.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.henrypeya.data.local.dao.UserDao
import com.henrypeya.data.local.entities.UserEntity
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.user.LoginRequestDto
import com.henrypeya.data.remote.dto.user.LoginResponseDto
import com.henrypeya.data.remote.dto.user.RegisterRequestDto
import com.henrypeya.data.remote.dto.user.RegisterResponseDto
import com.henrypeya.data.remote.dto.user.UserDto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userDao: UserDao

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: AuthRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                val tempDir = System.getProperty("java.io.tmpdir")
                File(tempDir, "test_prefs_${System.currentTimeMillis()}.preferences_pb")
            }
        )

        repository = AuthRepositoryImpl(
            apiService = apiService,
            applicationScope = testScope,
            userDao = userDao,
            dataStore = dataStore
        )
    }

    @Before
    fun clearDataStore(): Unit = runBlocking {
        dataStore.edit { it.clear() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success stores email as auth_token and inserts user`() = runTest {
        // ARRANGE
        val loginDto = LoginResponseDto(
            message = "Login exitoso",
            user = UserDto(
                id = "u123",
                email = "a@b.com",
                fullName = "Test User",
                nationality = null,
                imageUrl = null,
                password = "pw"
            )
        )
        whenever(apiService.loginUser(any())).thenReturn(loginDto)
        whenever(userDao.getUserById("u123")).thenReturn(null)

        val result = repository.login("  a@b.com  ", " pw ")

        assertTrue(result)

        val prefs = dataStore.data.first()
        assertEquals("a@b.com", prefs[PreferencesKeys.AUTH_TOKEN])
        assertEquals("u123", prefs[PreferencesKeys.USER_ID])
        assertEquals("a@b.com", prefs[PreferencesKeys.USER_EMAIL])

        assertTrue(repository.isLoggedIn().first())
        assertEquals("a@b.com", repository.getLoggedInUserEmail().first())
        assertEquals("u123", repository.getLoggedInUserId().first())

        verify(apiService).loginUser(
            LoginRequestDto(email = "a@b.com", password = "pw")
        )
        verify(userDao).getUserById("u123")
        verify(userDao).insertUser(any())
    }

    @Test
    fun `login with wrong message returns false`() = runTest {
        val loginDto = LoginResponseDto(
            message = "Credenciales incorrectas",
            user = UserDto(
                id = "u123",
                email = "a@b.com",
                fullName = "Test User",
                nationality = null,
                imageUrl = null,
                password = "pw"
            )
        )
        whenever(apiService.loginUser(any())).thenReturn(loginDto)

        val result = repository.login("a@b.com", "wrongpw")

        assertFalse(result)

        val prefs = dataStore.data.first()
        assertNull(prefs[PreferencesKeys.AUTH_TOKEN])
        assertFalse(repository.isLoggedIn().first())

        verify(userDao, never()).insertUser(any())
    }

    @Test
    fun `login failure returns false and user data not stored`() = runTest {
        whenever(apiService.loginUser(any())).thenThrow(RuntimeException("Network error"))

        val result = repository.login("x", "y")

        assertFalse(result)

        assertFalse(repository.isLoggedIn().first())
        assertNull(repository.getLoggedInUserEmail().first())
        assertNull(repository.getLoggedInUserId().first())

        verify(userDao, never()).insertUser(any())
    }

    @Test
    fun `register success stores user and inserts`() = runTest {
        val regResp = RegisterResponseDto(
            id = "rid",
            email = "e@e.com",
            fullName = "Full",
            userImageUrl = null,
            password = "pw"
        )
        whenever(apiService.registerUser(any())).thenReturn(regResp)

        val result = repository.register(" e@e.com ", " Full ", " pw ")

        assertTrue(result)

        assertEquals("rid", repository.getLoggedInUserId().first())
        assertEquals("e@e.com", repository.getLoggedInUserEmail().first())
        assertTrue(repository.isLoggedIn().first())

        val prefs = dataStore.data.first()
        assertEquals("e@e.com", prefs[PreferencesKeys.AUTH_TOKEN])

        verify(apiService).registerUser(
            RegisterRequestDto(
                email = "e@e.com",
                fullName = "Full",
                password = "pw"
            )
        )
        verify(userDao).insertUser(any())
    }

    @Test
    fun `register with empty id returns false`() = runTest {
        val regResp = RegisterResponseDto(
            id = "", // ID vacío
            email = "e@e.com",
            fullName = "Full",
            userImageUrl = null,
            password = "pw"
        )
        whenever(apiService.registerUser(any())).thenReturn(regResp)

        val result = repository.register("e@e.com", "Full", "pw")

        assertFalse(result)
        assertFalse(repository.isLoggedIn().first())
        verify(userDao, never()).insertUser(any())
    }

    @Test
    fun `register failure returns false`() = runTest {
        whenever(apiService.registerUser(any())).thenThrow(RuntimeException("Server error"))

        val result = repository.register("e@e.com", "Full", "pw")

        assertFalse(result)
        assertFalse(repository.isLoggedIn().first())
        verify(userDao, never()).insertUser(any())
    }

    @Test
    fun `logout clears stored user and calls DAO cleanup`() = runTest {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.AUTH_TOKEN] = "some_token"
            prefs[PreferencesKeys.USER_ID] = "user123"
            prefs[PreferencesKeys.USER_EMAIL] = "test@example.com"
        }

        assertTrue(repository.isLoggedIn().first())

        repository.logout()
        testScope.testScheduler.advanceUntilIdle()

        assertFalse(repository.isLoggedIn().first())
        assertNull(repository.getLoggedInUserEmail().first())
        assertNull(repository.getLoggedInUserId().first())

        val prefs = dataStore.data.first()
        assertNull(prefs[PreferencesKeys.AUTH_TOKEN])
        assertNull(prefs[PreferencesKeys.USER_ID])
        assertNull(prefs[PreferencesKeys.USER_EMAIL])

        verify(userDao).deleteAllUsers()
    }

    @Test
    fun `isLoggedIn returns true when auth token exists and not empty`() = runTest {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.AUTH_TOKEN] = "some_token"
        }

        assertTrue(repository.isLoggedIn().first())
    }

    @Test
    fun `isLoggedIn returns false when auth token is empty`() = runTest {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.AUTH_TOKEN] = ""
        }

        assertFalse(repository.isLoggedIn().first())
    }

    @Test
    fun `isLoggedIn returns false when no auth token stored`() = runTest {
        assertFalse(repository.isLoggedIn().first())
    }

    @Test
    fun `getLoggedInUserEmail returns stored email`() = runTest {
        val expectedEmail = "test@example.com"
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_EMAIL] = expectedEmail
        }

        assertEquals(expectedEmail, repository.getLoggedInUserEmail().first())
    }

    @Test
    fun `getLoggedInUserEmail returns null when no email stored`() = runTest {
        assertNull(repository.getLoggedInUserEmail().first())
    }

    @Test
    fun `getLoggedInUserId returns stored user id`() = runTest {
        val expectedUserId = "user123"
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_ID] = expectedUserId
        }

        assertEquals(expectedUserId, repository.getLoggedInUserId().first())
    }

    @Test
    fun `getLoggedInUserId returns null when no user id stored`() = runTest {
        assertNull(repository.getLoggedInUserId().first())
    }

    @Test
    fun `login preserves existing nationality from local database`() = runTest {
        val existingUser = UserEntity(
            id = "u123",
            email = "a@b.com",
            fullName = "Test User",
            nationality = "Argentina",
            imageUrl = null
        )

        val loginDto = LoginResponseDto(
            message = "Login exitoso",
            user = UserDto(
                id = "u123",
                email = "a@b.com",
                fullName = "Test User",
                nationality = null,
                imageUrl = null,
                password = "pw"
            )
        )

        whenever(apiService.loginUser(any())).thenReturn(loginDto)
        whenever(userDao.getUserById("u123")).thenReturn(existingUser)

        val result = repository.login("a@b.com", "pw")

        assertTrue(result)

        verify(userDao).insertUser(
            argThat { user ->
                user.id == "u123" &&
                        user.email == "a@b.com" &&
                        user.nationality == "Argentina"
            }
        )
    }
}