package com.henrypeya.core.model.domain.repository.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    val isLoggedInState: StateFlow<Boolean>

    suspend fun login(email: String, password: String): Boolean
    fun logout()
    fun isLoggedIn(): Flow<Boolean>
    suspend fun register(email: String, fullName: String, password: String): Boolean

    fun getLoggedInUserEmail(): Flow<String?>
    fun getLoggedInUserId(): Flow<String?>
}