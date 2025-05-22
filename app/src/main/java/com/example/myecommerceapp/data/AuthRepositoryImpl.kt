package com.example.myecommerceapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.myecommerceapp.domain.AuthRepository
import com.example.myecommerceapp.presentation.auth.AuthConstants
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val PREFS_FILE_NAME = "MyEcommerceAppPrefs"
private val KEY_IS_LOGGED_IN = "isLoggedIn"

@Singleton
class AuthRepositoryImpl
@Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    private val _isLoggedIn = MutableStateFlow(sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false))
    override fun isLoggedIn(): StateFlow<Boolean> = _isLoggedIn

    override fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            apply()
        }
        _isLoggedIn.value = loggedIn // Actualiza el valor de _isLoggedIn mi stateFlow
        Log.d("AuthRepositoryImpl", "isLoggedIn: ${_isLoggedIn.value}")
        }

    override fun registerUser(fullName: String, email: String, password: String): Boolean {
        //aca simulo el guardado de base pero en sharedpreferences
        val editor = sharedPreferences.edit()
        val userKey = "user_fullName_{$email}"
        if (sharedPreferences.contains(userKey)) {
            Log.d("AuthRepositoryImpl", "User already exists")
            return false
        }
        editor.putString(userKey, email)
        editor.putString("user_fullName_{$email}", fullName)
        editor.putString("user_password_${email}", password)
        editor.apply()
        Log.d("AuthRepositoryImpl", "User registered successfully")
        return true
    }

    override fun loginUser(email: String, password: String): Boolean {
        // Por ahora, simulamos una validación simple contra un usuario registrado
        val storedPassword = sharedPreferences.getString("user_password_${email}", null)
        val loginSuccess = (email == AuthConstants.TEST_EMAIL && password == AuthConstants.TEST_PASSWORD || storedPassword == password)
        if (loginSuccess) {
            Log.d("AuthRepositoryImpl", "User logged in successfully")
            setLoggedIn(true)
        }
        else {
            Log.d("AuthRepositoryImpl", "Invalid credentials")
        }
        return loginSuccess

    }

    override fun logout() {
    setLoggedIn(false)
        Log.d("AuthRepositoryImpl", "User logged out successfully")
    }

}




