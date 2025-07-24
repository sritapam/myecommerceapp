package com.henrypeya.data.repository.product

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.data.local.dao.ProductDao
import com.henrypeya.data.mappers.toEntityProduct
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.food.FoodResponseDto
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class ProductRepositoryImplTest {

 private val apiService = mockk<ApiService>()
 private val productDao = mockk<ProductDao>()
 private val repository = ProductRepositoryImpl(apiService, productDao)

 private val productDto = FoodResponseDto(
  id = "1",
  name = "Empanada",
  description = "Riquísima",
  price = 500.0,
  hasDrink = false,
  imageUrl = "image",
  category = "Comida"
 )

 private val domainProduct = Product(
  id = "1",
  name = "Empanada",
  description = "Riquísima",
  price = 500.0,
  hasDrink = false,
  imageUrl = "image",
  category = "Comida"
 )

 private val entityProduct = domainProduct.toEntityProduct()

 @Test
 fun `getProducts - when API returns data, products are stored and emitted`() = runTest {
  coEvery { apiService.getAllFoods() } returns listOf(productDto)
  coEvery { productDao.clearAllProducts() } just runs
  coEvery { productDao.insertProducts(listOf(entityProduct)) } just runs
  coEvery { productDao.getAllProducts() } returns flowOf(listOf(entityProduct))

  val result = repository.getProducts().toList().last()

  assertEquals(listOf(domainProduct), result)

  coVerifyOrder {
   apiService.getAllFoods()
   productDao.clearAllProducts()
   productDao.insertProducts(listOf(entityProduct))
  }
 }

 @Test
 fun `getProducts - when API fails with HttpException, emits cached products`() = runTest {
  coEvery { apiService.getAllFoods() } throws mockk<HttpException>()
  coEvery { productDao.getAllProducts() } returns flowOf(listOf(entityProduct))

  val result = repository.getProducts().first()

  assertEquals(listOf(domainProduct), result)
 }

 @Test
 fun `getProducts - when API fails with IOException, emits cached products`() = runTest {
  coEvery { apiService.getAllFoods() } throws IOException("No internet")
  coEvery { productDao.getAllProducts() } returns flowOf(listOf(entityProduct))

  val result = repository.getProducts().first()

  assertEquals(listOf(domainProduct), result)
 }

 @Test
 fun `getProducts - when API fails with generic Exception, emits cached products`() = runTest {
  coEvery { apiService.getAllFoods() } throws RuntimeException("Unknown error")
  coEvery { productDao.getAllProducts() } returns flowOf(listOf(entityProduct))

  val result = repository.getProducts().first()

  assertEquals(listOf(domainProduct), result)
 }
}