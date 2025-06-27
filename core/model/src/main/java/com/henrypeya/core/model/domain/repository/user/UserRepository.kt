package com.henrypeya.core.model.domain.repository.user

import com.henrypeya.core.model.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Flow<User>
    suspend fun updateUserProfile(user: User): Flow<User>
    suspend fun uploadProfileImage(imageData: Any): String
}