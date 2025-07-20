package com.henrypeya.feature_auth.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.R
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent
import com.henrypeya.feature_auth.ui.state.LoginState
import com.henrypeya.library.utils.ResourceProvider
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val resources: ResourceProvider
) : ViewModel() {

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
            _emailError.value = resources.getString(R.string.validation_error_email_empty)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = resources.getString(R.string.validation_error_email_invalid)
            return false
        }
        _emailError.value = null
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            _passwordError.value = resources.getString(R.string.validation_error_password_empty)
            return false
        }
        if (password.length < 8) {
            _passwordError.value = resources.getString(R.string.validation_error_password_too_short)
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
            _loginState.value =
                LoginState.Error(resources.getString(R.string.validation_error_form_generic))
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val loginSuccess = authRepository.login(email.value, password.value)

                if (loginSuccess) {
                    _loginState.value =
                        LoginState.Success(resources.getString(R.string.state_success_login))
                    _navigationEvents.send(
                        NavigationEvent.NavigateTo(
                            route = "main_app_graph",
                            popUpTo = "login_route",
                            inclusive = true
                        )
                    )
                } else {
                    _loginState.value =
                        LoginState.Error(resources.getString(R.string.state_error_invalid_credentials))
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    e.message ?: resources.getString(R.string.state_error_unknown_login)
                )
            }
        }
    }

    fun onMessageShown() {
        if (_loginState.value is LoginState.Error || _loginState.value is LoginState.Success) {
            _loginState.value = LoginState.Idle
        }
    }
}