package com.example.myecommerceapp.domain.repository

import com.example.myecommerceapp.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}