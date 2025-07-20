package com.henrypeya.feature_auth.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.ui.components.MessageType
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent
import com.henrypeya.feature_auth.ui.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _navigationEvents = Channel<NavigationEvent>()
    val navigationEvents =
        _navigationEvents.receiveAsFlow()

    val isFormValid: StateFlow<Boolean> =
        combine(
            email,
            password,
            emailError,
            passwordError
        ) { currentEmail, currentPassword, emailErr, passwordErr ->
            currentEmail.isNotBlank() && currentPassword.isNotBlank() && emailErr == null && passwordErr == null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), false)

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validatePassword(newPassword)
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            _emailError.value = "El email no puede estar vacío"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Formato de email inválido."
            return false
        }
        _emailError.value = null
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            _passwordError.value = "La contraseña no puede estar vacía."
            return false
        }
        if (password.length < 8) {
            _passwordError.value = "La contraseña debe tener al menos 8 caracteres."
            return false
        }
        _passwordError.value = null
        return true
    }

    fun login() {
        if (_loginState.value is LoginState.Error || _loginState.value is LoginState.Success) {
            _loginState.value = LoginState.Idle
        }

        val isEmailValid = validateEmail(email.value)
        val isPasswordValid = validatePassword(password.value)

        if (!isEmailValid || !isPasswordValid) {
            _loginState.value = LoginState.Error("Por favor, corrige los errores del formulario.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val loginSuccess = authRepository.login(email.value, password.value)

                if (loginSuccess) {
                    _loginState.value =
                        LoginState.Success("Inicio de sesión exitoso.")
                    _navigationEvents.send(
                        NavigationEvent.NavigateTo(
                            route = "main_app_graph",
                            popUpTo = "login_route",
                            inclusive = true
                        )
                    )
                } else {
                    _loginState.value = LoginState.Error("Credenciales inválidas.")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error durante login: ${e.message}", e)
                _loginState.value =
                    LoginState.Error(e.message ?: "Error desconocido al iniciar sesión.")
            }
        }
    }

    fun onMessageShown(messageType: MessageType) {
        if (_loginState.value is LoginState.Error || _loginState.value is LoginState.Success) {
            _loginState.value = LoginState.Idle
        }
    }
}