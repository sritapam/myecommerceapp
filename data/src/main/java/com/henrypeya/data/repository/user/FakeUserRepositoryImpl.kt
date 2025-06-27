package com.henrypeya.data.repository.user

import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.service.imageupload.CloudinaryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of UserRepository that stores user data in memory.
 * It uses FakeCloudinaryService to simulate image uploads.
 */

@Singleton
class FakeUserRepositoryImpl @Inject constructor(
    private val cloudinaryService: CloudinaryService
) : UserRepository {

    private val _userProfile = MutableStateFlow( //TODO : Refactorizar al tener el dato real
        User(
            id = "user123",
            name = "Juan",
            surname = "Perez",
            email = "juan.perez@example.com",
            nationality = "Argentina",
            imageUrl = "https://placehold.co/200x200/CCCCCC/000000?text=JP"
        )
    )

    override suspend fun getUserProfile(): Flow<User> = _userProfile.asStateFlow()

    override suspend fun updateUserProfile(user: User): Flow<User> {
        _userProfile.update { user }
        println("FakeUserRepository: User profile updated to: $user")
        return _userProfile.asStateFlow()
    }

    override suspend fun uploadProfileImage(imageData: Any): String {
        println("FakeUserRepository: Calling CloudinaryService to upload image...")
        val imageUrl = cloudinaryService.uploadImage(imageData)
        _userProfile.update { currentUser ->
            currentUser.copy(imageUrl = imageUrl)
        }
        println("FakeUserRepository: Profile image uploaded. New URL: $imageUrl. Profile updated.")
        return imageUrl
    }
}
