package com.example.myecommerceapp.presentation.auth.fragments.bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myecommerceapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onFullNameChange(newFullName: String) {
        _fullName.value = newFullName
        _errorMessage.value = null // Limpia el error al cambiar el texto
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null // Limpia el error al cambiar el texto
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _errorMessage.value = null // Limpia el error al cambiar el texto
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        _errorMessage.value = null // Limpia el error al cambiar el texto
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            // Validaciones
            if (email.value.isBlank() || fullName.value.isBlank() ||
                password.value.isBlank() || confirmPassword.value.isBlank()) {
                _errorMessage.value = "Todos los campos son obligatorios."
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                _errorMessage.value = "Formato de email inválido."
                return@launch
            }
            if (password.value.length < 6) {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres."
                return@launch
            }
            if (password.value != confirmPassword.value) {
                _errorMessage.value = "Las contraseñas no coinciden."
                return@launch
            }

            // Llamada al repositorio para registrar
            val registered = authRepository.registerUser(
                fullName.value,
                email.value,
                password.value
            )

            if (registered) {
                _registrationSuccess.value = true
                _errorMessage.value = null
            } else {
                // Mensaje de error general si el repositorio falla (ej. email ya registrado)
                _errorMessage.value = "El email ya está registrado o hubo un error al registrar."
                _registrationSuccess.value = false
            }
        }
    }

    fun registrationHandled() {
        // Importante: Resetea el valor a `false` para que la observación se active solo una vez por registro exitoso.
        _registrationSuccess.value = false
    }
}