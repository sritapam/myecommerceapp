package com.henrypeya.feature_auth.ui.state

interface AuthUiState {
    val isLoading: Boolean
    val successMessage: String?
    val errorMessage: String?
}