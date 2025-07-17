package com.henrypeya.core.model.domain.model.user

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val nationality: String,
    val imageUrl: String? = null
)