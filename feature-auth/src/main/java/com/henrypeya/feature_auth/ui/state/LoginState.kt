package com.henrypeya.feature_auth.ui.state

/**
 * Represents the different states of the login process.
 */
sealed class LoginState {
    /** The initial or idle state, waiting for user input. */
    object Idle : LoginState()

    /** Indicates that the login process is ongoing. */
    object Loading : LoginState()

    /** Indicates a successful login. */
    object Success : LoginState()

    /**
     * Represents an error state with a message describing the error.
     * @param message The error message to display.
     */
    data class Error(val message: String) : LoginState()
}