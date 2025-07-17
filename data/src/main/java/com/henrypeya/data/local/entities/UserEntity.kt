package com.henrypeya.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "fullName") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "nationality") val nationality: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?
)