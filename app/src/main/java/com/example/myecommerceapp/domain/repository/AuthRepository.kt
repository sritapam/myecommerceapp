package com.example.myecommerceapp.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun isLoggedIn(): StateFlow<Boolean>
    fun setLoggedIn(loggedIn: Boolean)
    fun registerUser(fullName: String, email: String, password: String) : Boolean
    fun loginUser(email: String, password: String) : Boolean
    fun logout()
}