package com.henrypeya.feature_auth.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.feature_auth.ui.state.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

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


// --- Funciones para actualizar el estado de los campos y validar ---

    fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
        validateFullName(newFullName)
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        validatePassword(newPassword)
        validateConfirmPassword(_confirmPassword.value)
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        validateConfirmPassword(newConfirmPassword)
    }

    // --- Lógica de Validación---

    private fun validateFullName(fullName: String): Boolean {
        if (fullName.isBlank()) {
            _fullNameError.value = "El nombre no puede estar vacío."
            return false
        }
        _fullNameError.value = null
        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            _emailError.value = "El email no puede estar vacío."
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private fun validateConfirmPassword(confirmPassword: String): Boolean {
        if (confirmPassword.isBlank()) {
            _confirmPasswordError.value = "Confirma tu contraseña."
            return false
        }
        if (confirmPassword != _password.value) {
            _confirmPasswordError.value = "Las contraseñas no coinciden."
            return false
        }
        _confirmPasswordError.value = null
        return true
    }

    // --- Lógica de Registro ---

    fun register() {
        val isFullNameValid = validateFullName(fullName.value)
        val isEmailValid = validateEmail(email.value)
        val isPasswordValid = validatePassword(password.value)
        val isConfirmPasswordValid = validateConfirmPassword(confirmPassword.value)

        if (!isFullNameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
            _registerState.value = RegisterState.Error("Por favor, corrige los errores del formulario.")
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val success = authRepository.register(email.value, password.value)

                if (success) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("Error al registrar usuario. Intenta de nuevo.")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido al registrarse.")
            }
        }
    }

    // Función para resetear el estado del error general
    fun errorShown() {
        _registerState.value = RegisterState.Idle
    }
}
