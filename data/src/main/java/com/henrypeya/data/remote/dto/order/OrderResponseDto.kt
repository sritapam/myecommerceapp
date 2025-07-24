package com.henrypeya.data.remote.dto.order

import com.google.gson.annotations.SerializedName

data class OrderResponseDto(
    @SerializedName("_id") val id: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("userEmail") val userEmail: String,
    @SerializedName("productIds") val items: List<OrderItemDto>,
    @SerializedName("total") val total: Double,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)
