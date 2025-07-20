package com.henrypeya.feature_auth.ui

import androidx.lifecycle.ViewModel
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedInState: StateFlow<Boolean> = authRepository.isLoggedInState
    fun isLoggedIn() = authRepository.isLoggedIn()
    fun logout() {
        authRepository.logout()
    }
}
