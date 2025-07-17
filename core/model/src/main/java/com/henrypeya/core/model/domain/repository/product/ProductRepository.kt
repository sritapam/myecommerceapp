package com.henrypeya.core.model.domain.repository.product

import com.henrypeya.core.model.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing product data.
 */
interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
}
