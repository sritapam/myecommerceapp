package com.example.myecommerceapp.view.ui.auth.fragments.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Para notificar que el login fue exitoso
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null // Clear error message when email changes
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _errorMessage.value = null // Clear error message when password changes
    }

    fun onLoginClick() {
        viewModelScope.launch {
            if (email.value.isBlank()) {
                _errorMessage.value = "El email no puede estar vacío"
                return@launch
            }
            if (password.value.isBlank()) {
                _errorMessage.value = "La contraseña no puede estar vacía"
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                _errorMessage.value = "Formato de email inválido."
                return@launch
            }

            val authenticated = authRepository.loginUser(email.value, password.value)
            if (authenticated) {
                authRepository.setLoggedIn(true)
                Log.d("LoginViewModel", "Login successful")
                _loginSuccess.value = true
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Credenciales inválidas"
                Log.d("LoginViewModel", "Invalid credentials")
                _loginSuccess.value = false
            }

        }
    }

    fun loginSuccessHandled() {
        _loginSuccess.value = false
    }

}