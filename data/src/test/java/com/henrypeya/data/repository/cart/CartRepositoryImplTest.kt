package com.henrypeya.data.repository.cart

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.data.local.dao.CartDao
import com.henrypeya.data.local.entities.CartItemEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CartRepositoryImplTest {

 private lateinit var cartDao: CartDao
 private lateinit var repository: CartRepositoryImpl
 private val testDispatcher = StandardTestDispatcher()

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  cartDao = mockk(relaxed = true)
  repository = CartRepositoryImpl(cartDao)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 private fun fakeProduct(id: String = "1") = Product(
  id = id,
  name = "Product $id",
  description = "Desc",
  price = 10.0,
  hasDrink = false,
  imageUrl = null,
  category = "Food"
 )

 private fun fakeEntity(product: Product, quantity: Int = 1) = CartItemEntity(
  productId = product.id,
  name = product.name,
  price = product.price,
  imageUrl = product.imageUrl,
  quantity = quantity,
  category = product.category
 )

 @Test
 fun `addProduct - inserts new item if not exists`() = runTest(testDispatcher) {
  val product = fakeProduct("123")
  coEvery { cartDao.getAllCartItems() } returns flowOf(emptyList())
  coEvery { cartDao.insertCartItem(any()) } just Runs

  repository.addProduct(product)

  coVerify {
   cartDao.insertCartItem(
    match {
     it.productId == "123" && it.quantity == 1
    }
   )
  }
 }

 @Test
 fun `addProduct - updates quantity if item exists`() = runTest(testDispatcher) {
  val product = fakeProduct("321")
  val existingItem = fakeEntity(product, quantity = 2)
  coEvery { cartDao.getAllCartItems() } returns flowOf(listOf(existingItem))
  coEvery { cartDao.updateCartItem(any()) } just Runs

  repository.addProduct(product)

  coVerify {
   cartDao.updateCartItem(
    match { it.productId == "321" && it.quantity == 3 }
   )
  }
 }

 @Test
 fun `updateCartItemQuantity - updates item if quantity positive`() = runTest(testDispatcher) {
  val product = fakeProduct("456")
  val existingItem = fakeEntity(product, quantity = 2)
  coEvery { cartDao.getAllCartItems() } returns flowOf(listOf(existingItem))
  coEvery { cartDao.updateCartItem(any()) } just Runs

  repository.updateCartItemQuantity("456", 5)

  coVerify {
   cartDao.updateCartItem(match { it.productId == "456" && it.quantity == 5 })
  }
 }

 @Test
 fun `updateCartItemQuantity - removes item if quantity zero`() = runTest(testDispatcher) {
  val product = fakeProduct("789")
  val existingItem = fakeEntity(product, quantity = 1)
  coEvery { cartDao.getAllCartItems() } returns flowOf(listOf(existingItem))
  coEvery { cartDao.deleteCartItem(any()) } just Runs

  repository.updateCartItemQuantity("789", 0)

  coVerify { cartDao.deleteCartItem("789") }
 }

 @Test
 fun `removeCartItem - calls DAO delete`() = runTest(testDispatcher) {
  coEvery { cartDao.deleteCartItem("111") } just Runs

  repository.removeCartItem("111")

  coVerify { cartDao.deleteCartItem("111") }
 }

 @Test
 fun `clearCart - calls DAO clearCart`() = runTest(testDispatcher) {
  coEvery { cartDao.clearCart() } just Runs

  repository.clearCart()

  coVerify { cartDao.clearCart() }
 }

 @Test
 fun `getCartItems - maps entities to domain model`() = runTest(testDispatcher) {
  val product = fakeProduct("1")
  val entity = fakeEntity(product, 2)
  coEvery { cartDao.getAllCartItems() } returns flowOf(listOf(entity))

  val result = repository.getCartItems().first()

  assertEquals(1, result.size)
  assertEquals("1", result[0].product.id)
  assertEquals(2, result[0].quantity)
 }
}
