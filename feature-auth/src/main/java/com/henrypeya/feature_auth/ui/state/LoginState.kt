package com.henrypeya.feature_auth.ui.state

/**
 * Represents the different states of the login process.
 */
sealed class LoginState : AuthUiState {
    object Idle : LoginState() {
        override val isLoading: Boolean = false
        override val successMessage: String? = null
        override val errorMessage: String? = null
    }

    object Loading : LoginState() {
        override val isLoading: Boolean = true
        override val successMessage: String? = null
        override val errorMessage: String? = null
    }

    data class Success(override val successMessage: String? = null) : LoginState() {
        override val isLoading: Boolean = false
        override val errorMessage: String? = null
    }

    data class Error(override val errorMessage: String) : LoginState() {
        override val isLoading: Boolean = false
        override val successMessage: String? = null
    }
}