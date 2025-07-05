package com.henrypeya.data.local.entities

//data class Product simple y usaremos un TypeConverter para serializar/deserializar la lista a/desde un String JSON
data class Product(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)