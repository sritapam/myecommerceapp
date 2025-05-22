package com.example.myecommerceapp.data.repository

import com.example.myecommerceapp.domain.model.Product
import com.example.myecommerceapp.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProductRepositoryImpl @Inject constructor() : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return listOf(
            Product(1, "Café", 3.50),
            Product(2, "Galletas", 1.00),
            Product(3, "Agua", 2.25)
        )
    }
}