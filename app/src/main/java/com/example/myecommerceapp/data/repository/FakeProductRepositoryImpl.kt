package com.example.myecommerceapp.data.repository

import com.example.myecommerceapp.domain.model.Product
import com.example.myecommerceapp.domain.repository.ProductRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProductRepositoryImpl @Inject constructor() : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        delay(500) // Simular retardo de red
        return listOf(
            Product(
                "P001",
                "Café Express",
                "Un café intenso y aromático.",
                3.50,
                true,
                "url_cafe_express"
            ),
            Product(
                "P002",
                "Galletas de Avena",
                "Galletas caseras con avena y pasas.",
                1.80,
                false,
                "url_galletas_avena"
            ),
            Product(
                "P003",
                "Jugo de Naranja Natural",
                "Jugo recién exprimido, 100% natural.",
                2.90,
                true,
                "url_jugo_naranja"
            ),
            Product(
                "P004",
                "Sandwich de Jamón y Queso",
                "Clásico sandwich en pan integral.",
                4.20,
                false,
                "url_sandwich"
            ),
            Product(
                "P005",
                "Muffin de Chocolate",
                "Delicioso muffin con trozos de chocolate.",
                2.50,
                false,
                "url_muffin"
            ),
            Product(
                "P006",
                "Té Verde Matcha",
                "Bebida energética y antioxidante.",
                3.80,
                true,
                "url_te_matcha"
            ),
            Product(
                "P007",
                "Brownie con Nuez",
                "Brownie suave con trozos de nuez.",
                3.00,
                false,
                "url_brownie"
            ),
            Product(
                "P008",
                "Smoothie de Frutos Rojos",
                "Batido refrescante de fresa, mora y arándano.",
                4.50,
                true,
                "url_smoothie"
            )
        )
    }
}