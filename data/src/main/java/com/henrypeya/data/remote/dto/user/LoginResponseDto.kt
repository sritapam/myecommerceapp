package com.henrypeya.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto
)