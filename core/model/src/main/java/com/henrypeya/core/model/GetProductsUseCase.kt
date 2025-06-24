package com.henrypeya.core.model

import javax.inject.Inject


class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(): List<Product> {
        return productRepository.getProducts()
    }

}