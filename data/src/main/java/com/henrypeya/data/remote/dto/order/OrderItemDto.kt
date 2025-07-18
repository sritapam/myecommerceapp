package com.henrypeya.data.remote.dto.order

import com.google.gson.annotations.SerializedName

data class OrderItemDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("hasDrink") val hasDrink: Boolean,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("category") val category: String,
)
