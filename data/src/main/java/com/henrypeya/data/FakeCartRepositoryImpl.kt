package com.henrypeya.data

import com.henrypeya.core.model.CartItem
import com.henrypeya.core.model.CartRepository
import com.henrypeya.core.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeCartRepositoryImpl @Inject constructor() : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun addProduct(product: Product) {
        delay(100)
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.product.id == product.id }
            if (existingItem != null) {
                currentItems.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            } else {
                currentItems + CartItem(product = product, quantity = 1)
            }
        }
        println("FakeCartRepository: Added/Updated product '${product.name}'. Current cart: ${_cartItems.value.size} items.")
    }

    override suspend fun removeProduct(productId: String) {
        delay(50)
        _cartItems.update { currentItems ->
            currentItems.filter { it.product.id != productId }
        }
        println("FakeCartRepository: Removed product ID '$productId'. Current cart: ${_cartItems.value.size} items.")
    }

    override suspend fun updateQuantity(productId: String, newQuantity: Int) {
        delay(50)
        _cartItems.update { currentItems ->
            if (newQuantity <= 0) {
                currentItems.filter { it.product.id != productId }
            } else {
                currentItems.map { item ->
                    if (item.product.id == productId) {
                        item.copy(quantity = newQuantity)
                    } else {
                        item
                    }
                }
            }
        }
        println("FakeCartRepository: Updated quantity for product ID '$productId' to $newQuantity. Current cart: ${_cartItems.value.size} items.")
    }

    override suspend fun clearCart() {
        delay(50)
        _cartItems.update { emptyList() }
        println("FakeCartRepository: Cart cleared.")
    }
}
