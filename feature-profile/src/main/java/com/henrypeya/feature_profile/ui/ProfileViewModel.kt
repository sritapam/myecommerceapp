package com.henrypeya.feature_profile.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.feature_profile.R
import com.henrypeya.feature_profile.ui.state.ProfileUiEvent
import com.henrypeya.feature_profile.ui.state.ProfileUiState
import com.henrypeya.library.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            user = User(
                id = "loading",
                fullName = resources.getString(R.string.profile_loading_user),
                email = resources.getString(R.string.profile_loading_email),
                nationality = resources.getString(R.string.profile_unknown_nationality),
                imageUrl = null
            )
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editableFullName = MutableStateFlow("")
    private val _editableEmail = MutableStateFlow("")
    private val _editableNationality = MutableStateFlow("")

    val editableFullName: StateFlow<String> = _editableFullName.asStateFlow()
    val editableEmail: StateFlow<String> = _editableEmail.asStateFlow()
    val editableNationality: StateFlow<String> = _editableNationality.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    private var areEditableFieldsInitialized = false

    init {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { user ->
                _uiState.update { it.copy(user = user) }

                if (!areEditableFieldsInitialized && user.id != "loading") {
                    _editableFullName.value = user.fullName
                    _editableEmail.value = user.email
                    _editableNationality.value = user.nationality

                    areEditableFieldsInitialized = true
                }
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
                            isEditing = false
                        )
                    }
                    _editableFullName.value = persistedUser.fullName
                    _editableEmail.value = persistedUser.email
                    _editableNationality.value = persistedUser.nationality
                    _uiEvent.emit(ProfileUiEvent.ShowSnackbar(resources.getString(R.string.message_profile_updated_success)))
                }
            } catch (e: Exception) {
                val errorMessage = resources.getString(R.string.message_profile_update_error, e.localizedMessage ?: "")
                _uiState.update { it.copy(errorMessage = errorMessage, isLoading = false) }
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

    fun uploadProfileImage(imageData: Any) {
        viewModelScope.launch {
            _uiState.update { it.copy(showImageUploadProgress = true, errorMessage = null) }
            try {
                val imageUrl = when (imageData) {
                    is Bitmap -> userRepository.uploadProfileImage(imageData)
                    is Uri -> userRepository.uploadProfileImage(imageData)
                    else -> throw IllegalArgumentException("Unsupported image data type")
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        user = currentState.user.copy(imageUrl = imageUrl),
                        showImageUploadProgress = false
                    )
                }
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar(resources.getString(R.string.message_image_upload_success)))
            } catch (e: Exception) {
                val errorMessage = resources.getString(R.string.message_image_upload_error, e.localizedMessage ?: "")
                _uiState.update { it.copy(errorMessage = errorMessage, showImageUploadProgress = false) }
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _uiState.update {
                    ProfileUiState(
                        user = User(
                            id = "logged_out",
                            fullName = resources.getString(R.string.profile_guest_user),
                            email = "",
                            nationality = "",
                            imageUrl = null
                        )
                    )
                }
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar(resources.getString(R.string.message_logout_success)))
            } catch (e: Exception) {
                val errorMessage = resources.getString(R.string.message_logout_error, e.localizedMessage ?: "")
                _uiState.update { it.copy(errorMessage = errorMessage) }
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar(errorMessage))
            }
        }
    }
}