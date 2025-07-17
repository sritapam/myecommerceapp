package com.henrypeya.data.local.entities

data class OrderItemEntity(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)
