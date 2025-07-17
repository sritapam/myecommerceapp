package com.henrypeya.data.mappers

import com.henrypeya.core.model.domain.model.user.User as DomainUser
import com.henrypeya.data.local.entities.UserEntity
import com.henrypeya.data.remote.dto.user.RegisterResponseDto
import com.henrypeya.data.remote.dto.user.UpdateUserProfileRequestDto
import com.henrypeya.data.remote.dto.user.UserDto

fun DomainUser.toEntityUser(): UserEntity {
    return UserEntity(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        nationality = this.nationality,
        imageUrl = this.imageUrl
    )
}

fun UserEntity.toDomainUser(): DomainUser {
    return DomainUser(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        nationality = this.nationality,
        imageUrl = this.imageUrl
    )
}

fun UserDto.toDomainUser(): DomainUser {
    return DomainUser(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        nationality = "",
        imageUrl = this.imageUrl
    )
}

fun RegisterResponseDto.toDomainUser(): DomainUser {
    return DomainUser(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        nationality = "",
        imageUrl = this.userImageUrl
    )
}

fun DomainUser.toUpdateProfileRequestDto(): UpdateUserProfileRequestDto {
    return UpdateUserProfileRequestDto(
        fullName = this.fullName,
        userImageUrl = this.imageUrl
    )
}
