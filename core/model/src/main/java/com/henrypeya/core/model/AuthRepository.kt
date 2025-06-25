package com.henrypeya.core.model

import kotlinx.coroutines.flow.Flow

interface AuthRepository : Flow<Boolean> {

    suspend fun login(email: String, password: String): Boolean
    fun logout()
    fun isLoggedIn(): Flow<Boolean>
    suspend fun register(email: String, password: String): Boolean
}