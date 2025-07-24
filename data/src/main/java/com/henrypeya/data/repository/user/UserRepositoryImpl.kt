package com.henrypeya.data.repository.user

import android.net.http.HttpException
import android.os.Build
import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.local.dao.UserDao
import com.henrypeya.data.service.imageupload.CloudinaryService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.annotation.RequiresExtension
import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.data.mappers.toDomainUser
import com.henrypeya.data.mappers.toEntityUser
import com.henrypeya.data.mappers.toUpdateProfileRequestDto
import com.henrypeya.data.remote.api.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.io.IOException

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cloudinaryService: CloudinaryService,
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : UserRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getUserProfile(): Flow<User> = flow {
        val userEmail = authRepository.getLoggedInUserEmail().firstOrNull()
        val userId = authRepository.getLoggedInUserId().firstOrNull()

        if (userEmail.isNullOrEmpty() || userId.isNullOrEmpty()) {
            emit(
                User(
                    id = "no_auth",
                    fullName = "Invitado",
                    email = "",
                    nationality = "",
                    imageUrl = null
                )
            )
            return@flow
        }

        try {
            val userDtoFromApi = apiService.getUserByEmail(userEmail)
            val domainUserFromApi = userDtoFromApi.toDomainUser()

            val existingLocalUser = userDao.getUserById(userId)
            val localNationality = existingLocalUser?.nationality ?: ""
            val localImageUrl = existingLocalUser?.imageUrl
            val finalDomainUser = domainUserFromApi.copy(
                nationality = localNationality,
                imageUrl = domainUserFromApi.imageUrl ?: localImageUrl
            )

            userDao.deleteAllUsers()
            userDao.insertUser(finalDomainUser.toEntityUser())

            emit(finalDomainUser)

        } catch (e: HttpException) {
            val localUserEntity = userDao.getUserById(userId)
            localUserEntity?.let { emit(it.toDomainUser()) } ?: run {
                emit(
                    User(
                        id = userId,
                        fullName = "Error de Carga",
                        email = userEmail,
                        nationality = "",
                        imageUrl = null
                    )
                )
            }
        } catch (e: IOException) {
            val localUserEntity = userDao.getUserById(userId)
            localUserEntity?.let { emit(it.toDomainUser()) } ?: run {
                emit(
                    User(
                        id = userId,
                        fullName = "Sin Conexión",
                        email = userEmail,
                        nationality = "",
                        imageUrl = null
                    )
                )
            }
        } catch (e: Exception) {
            val localUserEntity = userDao.getUserById(userId)
            localUserEntity?.let { emit(it.toDomainUser()) } ?: run {
                emit(
                    User(
                        id = userId,
                        fullName = "Error Desconocido",
                        email = userEmail,
                        nationality = "",
                        imageUrl = null
                    )
                )
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun updateUserProfile(user: User): Flow<User> = flow {
        val userEmail = authRepository.getLoggedInUserEmail().firstOrNull()
        val userId = authRepository.getLoggedInUserId().firstOrNull()

        if (userEmail.isNullOrEmpty() || userId.isNullOrEmpty() || userEmail != user.email) {
            throw SecurityException("No autorizado o email no coincide para actualizar este perfil.")
        }
        val nationalityToSaveLocally = user.nationality

        try {
            val updateRequestDto = user.toUpdateProfileRequestDto()

            val updatedUserDtoFromApi = apiService.updateUserInfo(userEmail, updateRequestDto)
            val domainUserFromApi = updatedUserDtoFromApi.toDomainUser()

            val finalDomainUser = domainUserFromApi.copy(
                nationality = nationalityToSaveLocally,
                imageUrl = domainUserFromApi.imageUrl ?: user.imageUrl
            )

            userDao.insertUser(finalDomainUser.toEntityUser())

            emit(finalDomainUser)

        } catch (e: HttpException) {
            val localUserToUpdate = user.toEntityUser()
            userDao.insertUser(localUserToUpdate)
            emit(user)
        } catch (e: IOException) {
            val localUserToUpdate = user.toEntityUser()
            userDao.insertUser(localUserToUpdate)
            emit(user)
        } catch (e: Exception) {
            val localUserToUpdate = user.toEntityUser()
            userDao.insertUser(localUserToUpdate)
            emit(user)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun uploadProfileImage(imageData: Any): String {
        val imageUrl = cloudinaryService.uploadImage(imageData)
        val userEmail = authRepository.getLoggedInUserEmail().firstOrNull()
        val userId = authRepository.getLoggedInUserId().firstOrNull()

        if (userEmail.isNullOrEmpty() || userId.isNullOrEmpty()) {
            throw IllegalStateException("User is not logged in to update profile image.")
        }

        val currentUserEntity = userDao.getUserById(userId)
        if (currentUserEntity != null) {
            val updatedUserEntity = currentUserEntity.copy(imageUrl = imageUrl)
            userDao.updateUser(updatedUserEntity)

            val currentDomainUserWithLocalNationality = currentUserEntity.toDomainUser()
            val userWithUpdatedImageAndLocalNationality =
                currentDomainUserWithLocalNationality.copy(imageUrl = imageUrl)

            try {
                updateUserProfile(userWithUpdatedImageAndLocalNationality).first()
            } catch (e: Exception) {
            throw RuntimeException("Error updating user profile with new image URL", e)
            }

        }
        return imageUrl
    }
}