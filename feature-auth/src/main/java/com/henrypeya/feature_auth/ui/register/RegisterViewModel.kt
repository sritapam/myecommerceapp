package com.henrypeya.feature_auth.ui.register

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.R
import com.henrypeya.feature_auth.ui.navigation.NavigationEvent
import com.henrypeya.feature_auth.ui.state.RegisterState
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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val resources: ResourceProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_FULL_NAME = "register_full_name"
        private const val KEY_EMAIL = "register_email"
        private const val KEY_PASSWORD = "register_password"
        private const val KEY_CONFIRM_PASSWORD = "register_confirm_password"
    }

    val fullName: StateFlow<String> = savedStateHandle.getStateFlow(KEY_FULL_NAME, "")
    val email: StateFlow<String> = savedStateHandle.getStateFlow(KEY_EMAIL, "")
    val password: StateFlow<String> = savedStateHandle.getStateFlow(KEY_PASSWORD, "")
    val confirmPassword: StateFlow<String> = savedStateHandle.getStateFlow(KEY_CONFIRM_PASSWORD, "")

    private val _fullNameError = MutableStateFlow<String?>(null)
    val fullNameError: StateFlow<String?> = _fullNameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _navigationEvents = Channel<NavigationEvent>()
    val navigationEvents = _navigationEvents.receiveAsFlow()


    val isFormValid: StateFlow<Boolean> = combine(
        fullName, email, password, confirmPassword,
        fullNameError, emailError, passwordError, confirmPasswordError
    ) { values ->
        val currentFullName = values[0] as String
        val currentEmail = values[1] as String
        val currentPassword = values[2] as String
        val currentConfirmPassword = values[3] as String
        val currentFullNameErr = values[4]
        val currentEmailErr = values[5]
        val currentPasswordErr = values[6]
        val currentConfirmPasswordErr = values[7]

        currentFullName.isNotBlank() && currentEmail.isNotBlank() &&
                currentPassword.isNotBlank() && currentConfirmPassword.isNotBlank() &&
                currentFullNameErr == null && currentEmailErr == null &&
                currentPasswordErr == null && currentConfirmPasswordErr == null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onFullNameChange(newFullName: String) {
        savedStateHandle[KEY_FULL_NAME] = newFullName
        validateFullName(newFullName)
    }

    fun onEmailChange(newEmail: String) {
        savedStateHandle[KEY_EMAIL] = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        savedStateHandle[KEY_PASSWORD] = newPassword
        validatePassword(newPassword)
        validateConfirmPassword(confirmPassword.value)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        savedStateHandle[KEY_CONFIRM_PASSWORD] = newConfirmPassword
        validateConfirmPassword(newConfirmPassword)
    }

    private fun validateFullName(fullName: String): Boolean {
        if (fullName.isBlank()) {
            _fullNameError.value = resources.getString(R.string.validation_error_fullname_empty)
            return false
        }
        _fullNameError.value = null
        return true
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

    private fun validateConfirmPassword(confirmPassword: String): Boolean {
        if (confirmPassword.isBlank()) {
            _confirmPasswordError.value =
                resources.getString(R.string.validation_error_confirm_password_empty)
            return false
        }
        if (confirmPassword != password.value) {
            _confirmPasswordError.value =
                resources.getString(R.string.validation_error_passwords_no_match)
            return false
        }
        _confirmPasswordError.value = null
        return true
    }

    fun register() {
        if (_registerState.value is RegisterState.Error || _registerState.value is RegisterState.Success) {
            _registerState.value = RegisterState.Idle
        }

        val isFullNameValid = validateFullName(fullName.value)
        val isEmailValid = validateEmail(email.value)
        val isPasswordValid = validatePassword(password.value)
        val isConfirmPasswordValid = validateConfirmPassword(confirmPassword.value)

        if (!isFullNameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
            _registerState.value =
                RegisterState.Error(resources.getString(R.string.validation_error_form_generic))
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val success = authRepository.register(email.value, fullName.value, password.value)

                if (success) {
                    _registerState.value =
                        RegisterState.Success(resources.getString(R.string.state_success_register))
                    _navigationEvents.send(
                        NavigationEvent.NavigateTo(
                            route = "main_app_graph",
                            popUpTo = "register_route",
                            inclusive = true
                        )
                    )
                } else {
                    _registerState.value =
                        RegisterState.Error(resources.getString(R.string.state_error_register_failed))
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    e.message ?: resources.getString(R.string.state_error_unknown_register)
                )
            }
        }
    }

    fun onMessageShown() {
        if (_registerState.value is RegisterState.Error || _registerState.value is RegisterState.Success) {
            _registerState.value = RegisterState.Idle
        }
    }
}
