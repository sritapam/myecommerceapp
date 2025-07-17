package com.henrypeya.data.repository.product

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.food.FoodResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        return try {
            val foodResponseDto = apiService.getAllFoods()
            foodResponseDto.map { it.toDomainProduct() }
        } catch (e: Exception) {
            println("Error fetching products from API: ${e.message}")
            throw e
        }
    }

    private fun FoodResponseDto.toDomainProduct(): Product {
        return Product(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            hasDrink = this.hasDrink,
            imageUrl = this.imageUrl
        )
    }
}