package com.henrypeya.core.model.domain.usecase.product

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import javax.inject.Inject


class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(): List<Product> {
        return productRepository.getProducts()
    }

}