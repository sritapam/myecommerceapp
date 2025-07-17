package com.henrypeya.data.remote.dto.order

import com.google.gson.annotations.SerializedName

data class OrderRequestDto(
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("productIds") val items: List<OrderItemDto>,
    @SerializedName("total") val total: Double,
    @SerializedName("timestamp") val timestamp: Long
)
