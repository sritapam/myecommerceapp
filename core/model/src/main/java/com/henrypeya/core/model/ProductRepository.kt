package com.henrypeya.core.model


interface ProductRepository {
    suspend fun getProducts(): List<Product>
    // suspend fun getProductById(productId: String): Product?
}