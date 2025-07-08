package com.henrypeya.feature_auth.ui.state

/**
 * Represents the different states of the login process.
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}