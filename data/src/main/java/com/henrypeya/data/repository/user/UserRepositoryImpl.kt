package com.henrypeya.data.repository.user

import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.local.dao.UserDao
import com.henrypeya.data.local.entities.UserEntity
import com.henrypeya.data.service.imageupload.CloudinaryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import kotlinx.coroutines.flow.firstOrNull

/**
 * Fake implementation of UserRepository that stores user data in memory.
 * It uses FakeCloudinaryService to simulate image uploads.
 */

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cloudinaryService: CloudinaryService
) : UserRepository {

    private fun UserEntity.toDomainUser(): User {
        return User(
            id = this.id,
            name = this.name,
            surname = this.surname,
            email = this.email,
            nationality = this.nationality,
            imageUrl = this.imageUrl
        )
    }

    private fun User.toEntityUser(): UserEntity {
        return UserEntity(
            id = this.id,
            name = this.name,
            surname = this.surname,
            email = this.email,
            nationality = this.nationality,
            imageUrl = this.imageUrl
        )
    }

    override suspend fun getUserProfile(): Flow<User> {
        return userDao.getUserProfile().map { userEntity ->
            userEntity?.toDomainUser() ?: User(
                id = "default",
                name = "",
                surname = "",
                email = "",
                nationality = "",
                imageUrl = null
            )
        }
    }

    override suspend fun updateUserProfile(user: User): Flow<User> {
        Log.d("UserRepositoryImpl", "Updating user profile in DB: $user")
        userDao.insertUser(user.toEntityUser())
        Log.d("UserRepositoryImpl", "User profile updated in DB.")
        return getUserProfile()
    }

    override suspend fun uploadProfileImage(imageData: Any): String {
        Log.d("UserRepositoryImpl", "Calling CloudinaryService to upload image...")
        val imageUrl = cloudinaryService.uploadImage(imageData)
        Log.d("UserRepositoryImpl", "Profile image uploaded. New URL: $imageUrl.")

        val currentUserEntity = userDao.getUserProfile().map { it }.firstOrNull()
        if (currentUserEntity != null) {
            val updatedUserEntity = currentUserEntity.copy(imageUrl = imageUrl)
            userDao.updateUser(updatedUserEntity)
            Log.d("UserRepositoryImpl", "Image URL updated in local DB.")
        } else {
            //TODO PREGUNTAR A QUIEN SE LE QUIERE ACTUALIZAR LA IMAGEN
            Log.w("UserRepositoryImpl", "No existing user found in DB to update image URL.")
        }
        return imageUrl
    }
}
