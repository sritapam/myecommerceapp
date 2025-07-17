package com.henrypeya.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("encryptedPassword") val encryptedPassword: String
)