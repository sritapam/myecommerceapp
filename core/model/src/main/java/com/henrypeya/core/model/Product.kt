package com.henrypeya.core.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val includesDrink: Boolean,
    val imageUrl: String? = null
)