package com.henrypeya.core.model.domain.repository.product

import com.henrypeya.core.model.domain.model.product.Product


interface ProductRepository {
    suspend fun getProducts(): List<Product>
    // suspend fun getProductById(productId: String): Product?
}