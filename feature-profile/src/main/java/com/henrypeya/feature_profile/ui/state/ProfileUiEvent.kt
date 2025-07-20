package com.henrypeya.feature_profile.ui.state

sealed class ProfileUiEvent {
    data class ShowSnackbar(val message: String) : ProfileUiEvent()
}
