package com.henrypeya.data.local.entities

/**
 * Represents a product entity for local storage.
 * Used to store and retrieve product information efficiently.
 */
data class Product(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)