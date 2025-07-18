package com.henrypeya.data.repository.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.user.RegisterRequestDto
import com.henrypeya.data.remote.dto.user.LoginRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.henrypeya.data.local.dao.UserDao
import com.henrypeya.data.mappers.toDomainUser
import com.henrypeya.data.mappers.toEntityUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

private object PreferencesKeys {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
}

@Singleton
@ExperimentalCoroutinesApi
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val apiService: ApiService,
    private val applicationScope: CoroutineScope,
    private val userDao: UserDao
) : AuthRepository {

    override val isLoggedInState: StateFlow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]?.isNotEmpty() == true
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        Log.d("AuthRepositoryImpl", "AuthRepositoryImpl inicializado.")
    }

    override suspend fun login(email: String, password: String): Boolean {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        try {
            val response = apiService.loginUser(
                LoginRequestDto(email = trimmedEmail, password = trimmedPassword)
            )

            if (response.message == "Login exitoso") {
                saveAuthToken(response.user.email)
                saveUserId(response.user.id)
                saveUserEmail(response.user.email)

                val userDomainFromApi = response.user.toDomainUser()
                val existingUserEntity = userDao.getUserById(userDomainFromApi.id)
                val localNationality = existingUserEntity?.nationality ?: ""

                val userToPersist = userDomainFromApi.copy(nationality = localNationality)

                userDao.insertUser(userToPersist.toEntityUser())
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error durante el login: ${e.localizedMessage}", e)
            return false
        }
    }

    override fun logout() {
        applicationScope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.AUTH_TOKEN)
                preferences.remove(PreferencesKeys.USER_ID)
                preferences.remove(PreferencesKeys.USER_EMAIL)
            }
            userDao.deleteAllUsers()
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN]?.isNotEmpty() == true
        }
    }

    override suspend fun register(email: String, fullName: String, password: String): Boolean {
        val trimmedEmail = email.trim()
        val trimmedFullName = fullName.trim()
        val trimmedPassword = password.trim()

        try {
            val response = apiService.registerUser(
                RegisterRequestDto(email = trimmedEmail, fullName = trimmedFullName, password = trimmedPassword)
            )

            if (response.id.isNotEmpty()) {
                saveAuthToken(response.email)
                saveUserId(response.id)
                saveUserEmail(response.email)

                val userDomainFromApi = response.toDomainUser()

                userDao.insertUser(userDomainFromApi.toEntityUser())
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Error durante el registro: ${e.localizedMessage}", e)
            return false
        }
    }

    private suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
        }
    }

    private suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    private suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }

    override fun getLoggedInUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL]
        }
    }

    override fun getLoggedInUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }
    }
}
