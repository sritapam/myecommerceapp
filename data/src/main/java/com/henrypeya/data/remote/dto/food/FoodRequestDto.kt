package com.henrypeya.data.remote.dto.food

import com.google.gson.annotations.SerializedName

data class FoodRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("price") val price: Double,
    @SerializedName("hasDrink") val hasDrink: Boolean,
    @SerializedName("category") val category: String
)
