package com.henrypeya.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class RegisterResponseDto(
    @SerializedName("_id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("userImageUrl") val userImageUrl: String? = null,
    @SerializedName("password") val password: String
)
