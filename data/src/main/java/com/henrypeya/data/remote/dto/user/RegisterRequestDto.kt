package com.henrypeya.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("password") val password: String
)