package com.example.myecommerceapp.presentation.main

import androidx.lifecycle.ViewModel
import com.example.myecommerceapp.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

//    fun isUserLoggedIn(): StateFlow<Boolean> {
//        return authRepository.isLoggedIn()
//    }

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn()
}