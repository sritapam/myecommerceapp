package com.example.myecommerceapp.data

import com.example.myecommerceapp.domain.Product
import com.example.myecommerceapp.domain.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProductRepository @Inject constructor() : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return listOf(
            Product(1, "Café", 3.50),
            Product(2, "Galletas", 1.00),
            Product(3, "Agua", 2.25)
        )
    }
}