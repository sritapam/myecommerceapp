package com.henrypeya.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = false)
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?
)