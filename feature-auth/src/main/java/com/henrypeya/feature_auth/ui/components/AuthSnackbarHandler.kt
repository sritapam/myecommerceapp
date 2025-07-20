package com.henrypeya.feature_auth.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.henrypeya.feature_auth.ui.state.AuthUiState
import kotlinx.coroutines.launch

enum class MessageType {
    Error, Success
}
@Composable
fun AuthSnackbarHandler(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onMessageShown: (messageType: MessageType) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = true
                )
                onMessageShown(MessageType.Error)
            }
        }

        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    withDismissAction = false
                )
                onMessageShown(MessageType.Success)
            }
        }
    }
}
