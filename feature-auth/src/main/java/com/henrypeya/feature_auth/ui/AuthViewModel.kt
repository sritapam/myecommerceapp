package com.henrypeya.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedInState: StateFlow<Boolean> = authRepository.isLoggedIn()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), // Empieza a recolectar cuando hay suscriptores
            initialValue = false
        )

    // Método para obtener el Flow del estado de login (usado por MainActivity)
    fun isLoggedIn() = authRepository.isLoggedIn()

    fun logout() {
        authRepository.logout()
        // Lógica adicional de limpieza al necesitarlo
    }
}
