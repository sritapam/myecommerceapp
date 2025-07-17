package com.henrypeya.feature_profile.ui.state

import com.henrypeya.core.model.domain.model.user.User

data class ProfileUiState(
    val user: User = User(id = "", fullName = "", email = "", nationality = "", imageUrl = null),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEditing: Boolean = false,
    val showImageUploadProgress: Boolean = false
)