package com.example.myecommerceapp.domain

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}