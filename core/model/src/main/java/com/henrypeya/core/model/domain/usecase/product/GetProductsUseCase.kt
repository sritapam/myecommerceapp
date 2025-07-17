package com.henrypeya.core.model.domain.usecase.product

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a list of products.
 */
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getProducts()
    }

}