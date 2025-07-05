package com.henrypeya.core.model.domain.repository.cart

import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Cart data source.
 * Defines the contract for managing shopping cart items.
 */
interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addProduct(product: Product)
    suspend fun removeCartItem(productId: String)
    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int)
    suspend fun clearCart()
}