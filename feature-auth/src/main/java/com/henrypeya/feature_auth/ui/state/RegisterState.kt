package com.henrypeya.feature_auth.ui.state

/**
 * Represents the different states of the registration process.
 */
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}