package com.example.myecommerceapp.domain.usecase

import com.example.myecommerceapp.domain.model.Product
import com.example.myecommerceapp.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(): List<Product> {
        return productRepository.getProducts()
    }

}