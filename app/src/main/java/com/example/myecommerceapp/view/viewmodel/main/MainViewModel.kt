package com.example.myecommerceapp.view.ui.main

import androidx.lifecycle.ViewModel
import com.henrypeya.core.model.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: com.henrypeya.core.model.AuthRepository
): ViewModel() {

//    fun isUserLoggedIn(): StateFlow<Boolean> {
//        return authRepository.isLoggedIn()
//    }

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn()
}
