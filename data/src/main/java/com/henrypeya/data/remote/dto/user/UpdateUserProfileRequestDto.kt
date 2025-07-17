package com.henrypeya.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UpdateUserProfileRequestDto(
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("userImageUrl") val userImageUrl: String? = null
)
