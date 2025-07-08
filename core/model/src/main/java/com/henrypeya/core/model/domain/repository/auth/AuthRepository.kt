package com.henrypeya.core.model.domain.repository.auth

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    fun logout()
    val isLoggedInState: StateFlow<Boolean>
    fun isLoggedIn(): Flow<Boolean>
    suspend fun register(email: String, password: String): Boolean
}