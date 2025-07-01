package com.henrypeya.feature_profile.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.user.User
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            user = User(
                id = "temp",
                name = "",
                surname = "",
                email = "",
                nationality = ""
            )
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editableName = MutableStateFlow("")
    private val _editableSurname = MutableStateFlow("")
    private val _editableEmail = MutableStateFlow("")
    private val _editableNationality = MutableStateFlow("")

    init {
        viewModelScope.launch {
            userRepository.getUserProfile().collectLatest { user ->
                _uiState.update { it.copy(user = user) }
                _editableName.value = user.name
                _editableSurname.value = user.surname
                _editableEmail.value = user.email
                _editableNationality.value = user.nationality
            }
            Log.d("ProfileViewModel", "User profile loaded: ${_uiState.value.user}")
        }
    }

    fun onNameChange(newName: String) {
        _editableName.value = newName
    }

    fun onSurnameChange(newSurname: String) {
        _editableSurname.value = newSurname
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
            _editableName.value = uiState.value.user.name
            _editableSurname.value = uiState.value.user.surname
            _editableEmail.value = uiState.value.user.email
            _editableNationality.value = uiState.value.user.nationality
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val updatedUser = uiState.value.user.copy(
                    name = _editableName.value,
                    surname = _editableSurname.value,
                    email = _editableEmail.value,
                    nationality = _editableNationality.value
                )
                userRepository.updateUserProfile(updatedUser)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditing = false,
                        errorMessage = "Perfil actualizado exitosamente."
                    )
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
                Log.d("ProfileViewModel", "Image upload finished. New URL set in UI state: $imageUrl")

                _uiState.update {
                    it.copy(
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
                Log.e("ProfileViewModel", "Error uploading image: ${e.localizedMessage ?: "Desconocido"}")
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    val editableName: StateFlow<String> = _editableName.asStateFlow()
    val editableSurname: StateFlow<String> = _editableSurname.asStateFlow()
    val editableEmail: StateFlow<String> = _editableEmail.asStateFlow()
    val editableNationality: StateFlow<String> = _editableNationality.asStateFlow()
}
