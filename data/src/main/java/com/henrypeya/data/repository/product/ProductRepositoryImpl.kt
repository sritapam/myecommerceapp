package com.henrypeya.data.repository.product

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import com.henrypeya.data.local.dao.ProductDao
import com.henrypeya.data.mappers.toDomainProduct
import com.henrypeya.data.mappers.toEntityProduct
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.food.FoodResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun getProducts(): Flow<List<Product>> = flow {
        try {
            val foodResponseDto = apiService.getAllFoods()
            val domainProducts = foodResponseDto.map { it.toDomainProduct() }

            productDao.clearAllProducts()
            productDao.insertProducts(domainProducts.map { it.toEntityProduct() })
            emit(domainProducts)

        } catch (e: HttpException) {
            val cachedProducts = productDao.getAllProducts().map { entities ->
                entities.map { it.toDomainProduct() }
            }.first()
            emit(cachedProducts)
        } catch (e: IOException) {
            val cachedProducts = productDao.getAllProducts().map { entities ->
                entities.map { it.toDomainProduct() }
            }.first()
            emit(cachedProducts)
        } catch (e: Exception) {
            val cachedProducts = productDao.getAllProducts().map { entities ->
                entities.map { it.toDomainProduct() }
            }.first()
            emit(cachedProducts)
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