package com.henrypeya.feature_auth.ui.state

/**
 * Represents the different states of the registration process.
 */
sealed class RegisterState : AuthUiState {
    object Idle : RegisterState() {
        override val isLoading: Boolean = false
        override val successMessage: String? = null
        override val errorMessage: String? = null
    }

    object Loading : RegisterState() {
        override val isLoading: Boolean = true
        override val successMessage: String? = null
        override val errorMessage: String? = null
    }

    data class Success(override val successMessage: String? = null) : RegisterState() {
        override val isLoading: Boolean = false
        override val errorMessage: String? = null
    }

    data class Error(override val errorMessage: String) : RegisterState() {
        override val isLoading: Boolean = false
        override val successMessage: String? = null
    }
}