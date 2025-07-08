package com.henrypeya.data.repository.auth

import android.content.Context
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "auth_prefs"
private const val AUTH_TOKEN_KEY = "auth_token"
private const val USER_ID_KEY = "user_id"

@Singleton
@ExperimentalCoroutinesApi
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedInState: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    @Volatile
    private var registeredUsers = mutableListOf<Pair<String, String>>().apply {
        add(AuthConstants.TEST_EMAIL to AuthConstants.TEST_PASSWORD)
    }

    init {
        val token = getAuthToken()
        _isLoggedIn.value = !token.isNullOrEmpty()
    }

    override suspend fun login(email: String, password: String): Boolean {
        delay(500)

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        val userExists = registeredUsers.any { it.first == trimmedEmail && it.second == trimmedPassword }

        if (userExists) {
            saveAuthToken("dummy_token_for_${trimmedEmail}")
            saveUserId("user_id_${trimmedEmail.hashCode()}")
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    override fun logout() {
        clearAuthToken()
        clearUserId()
        _isLoggedIn.value = false
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return _isLoggedIn.asStateFlow()
    }

    override suspend fun register(email: String, password: String): Boolean {
        delay(500)

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (registeredUsers.any { it.first == trimmedEmail }) {
            return false
        }

        registeredUsers.add(trimmedEmail to trimmedPassword)

        val dummyToken = "mock_auth_token_${System.currentTimeMillis()}"
        val dummyUserId = "mock_user_${System.currentTimeMillis()}"

        saveAuthToken(dummyToken)
        saveUserId(dummyUserId)
        _isLoggedIn.value = true
        return true
    }

    private fun getAuthToken(): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(AUTH_TOKEN_KEY, null)
        return token
    }

    private fun clearAuthToken() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(AUTH_TOKEN_KEY).apply() //todo revisar
    }

    private fun saveUserId(userId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(USER_ID_KEY, userId).apply()
    }

    private fun getUserId(): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userId = prefs.getString(USER_ID_KEY, null)
        return userId
    }

    private fun clearUserId() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(USER_ID_KEY).apply()
    }

    private fun saveAuthToken(token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }
}
