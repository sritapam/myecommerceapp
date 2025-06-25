package com.henrypeya.data

import com.henrypeya.core.model.ProductRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeProductRepositoryImpl @Inject constructor() : ProductRepository {
    override suspend fun getProducts(): List<com.henrypeya.core.model.Product> {
        delay(500) 
        return listOf(
            com.henrypeya.core.model.Product(
                "P001",
                "Café Express",
                "Un café intenso y aromático.",
                3.50,
                true,
                "url_cafe_express"
            ),
            com.henrypeya.core.model.Product(
                "P002",
                "Galletas de Avena",
                "Galletas caseras con avena y pasas.",
                1.80,
                false,
                "url_galletas_avena"
            ),
            com.henrypeya.core.model.Product(
                "P003",
                "Jugo de Naranja Natural",
                "Jugo recién exprimido, 100% natural.",
                2.90,
                true,
                "url_jugo_naranja"
            ),
            com.henrypeya.core.model.Product(
                "P004",
                "Sandwich de Jamón y Queso",
                "Clásico sandwich en pan integral.",
                4.20,
                false,
                "url_sandwich"
            ),
            com.henrypeya.core.model.Product(
                "P005",
                "Muffin de Chocolate",
                "Delicioso muffin con trozos de chocolate.",
                2.50,
                false,
                "url_muffin"
            ),
            com.henrypeya.core.model.Product(
                "P006",
                "Té Verde Matcha",
                "Bebida energética y antioxidante.",
                3.80,
                true,
                "url_te_matcha"
            ),
            com.henrypeya.core.model.Product(
                "P007",
                "Brownie con Nuez",
                "Brownie suave con trozos de nuez.",
                3.00,
                false,
                "url_brownie"
            ),
            com.henrypeya.core.model.Product(
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