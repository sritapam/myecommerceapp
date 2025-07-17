package com.henrypeya.feature_profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.feature_profile.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            user = User(
                id = "loading",
                fullName = "Cargando...",
                email = "cargando@ejemplo.com",
                nationality = "Desconocida",
                imageUrl = null
            )
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editableFullName = MutableStateFlow("")
    private val _editableEmail = MutableStateFlow("")
    private val _editableNationality = MutableStateFlow("")

    init {
        viewModelScope.launch {
            userRepository.getUserProfile().collectLatest { user ->
                _uiState.update { it.copy(user = user) }
                _editableFullName.value = user.fullName
                _editableEmail.value = user.email
                _editableNationality.value = user.nationality
            }
        }
    }

    fun onFullNameChange(newFullName: String) {
        _editableFullName.value = newFullName
    }

    fun onEmailChange(newEmail: String) {
        _editableEmail.value = newEmail
    }

    fun onNationalityChange(newNationality: String) {
        _editableNationality.value = newNationality
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }

        if (!uiState.value.isEditing) {
            _editableFullName.value = uiState.value.user.fullName
            _editableEmail.value = uiState.value.user.email
            _editableNationality.value = uiState.value.user.nationality
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val updatedUser = uiState.value.user.copy(
                    fullName = _editableFullName.value,
                    email = _editableEmail.value,
                    nationality = _editableNationality.value
                )

                userRepository.updateUserProfile(updatedUser).collectLatest { persistedUser ->
                    _uiState.update {
                        it.copy(
                            user = persistedUser,
                            isLoading = false,
                            isEditing = false,
                            errorMessage = "Perfil actualizado exitosamente."
                        )
                    }
                    _editableFullName.value = persistedUser.fullName
                    _editableEmail.value = persistedUser.email
                    _editableNationality.value = persistedUser.nationality
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar perfil: ${e.localizedMessage ?: "Desconocido"}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun uploadProfileImage(imageData: Any) {
        viewModelScope.launch {
            _uiState.update { it.copy(showImageUploadProgress = true, errorMessage = null) }
            try {
                val imageUrl = userRepository.uploadProfileImage(imageData)

                _uiState.update { currentState ->
                    currentState.copy(
                        user = currentState.user.copy(imageUrl = imageUrl),
                        showImageUploadProgress = false,
                        errorMessage = "Imagen subida exitosamente."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al subir imagen: ${e.localizedMessage ?: "Desconocido"}",
                        showImageUploadProgress = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _uiState.update { it.copy(errorMessage = "Sesión cerrada exitosamente.") }
                _uiState.value = ProfileUiState(
                    user = User(id = "logged_out", fullName = "Invitado", email = "", nationality = "", imageUrl = null),
                    isEditing = false, isLoading = false, errorMessage = null, showImageUploadProgress = false
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al cerrar sesión: ${e.localizedMessage ?: "Desconocido"}") }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    val editableFullName: StateFlow<String> = _editableFullName.asStateFlow()
    val editableEmail: StateFlow<String> = _editableEmail.asStateFlow()
    val editableNationality: StateFlow<String> = _editableNationality.asStateFlow()
}