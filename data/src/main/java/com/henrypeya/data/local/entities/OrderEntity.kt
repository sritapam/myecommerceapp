package com.henrypeya.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val orderIdApi: String? = null,
    val date: Date,
    val total: Double,
    val productsJson: String,
    val isSynced: Boolean = false,
    val category: String?
)