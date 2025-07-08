package com.henrypeya.core.model.domain.repository.product

import com.henrypeya.core.model.domain.model.product.Product

/**
 * Repository interface for accessing product data.
 */
interface ProductRepository {
    suspend fun getProducts(): List<Product>
    // suspend fun getProductById(productId: String): Product?
}