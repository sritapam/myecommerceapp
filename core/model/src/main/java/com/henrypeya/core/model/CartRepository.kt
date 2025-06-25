package com.henrypeya.core.model

import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Cart data source.
 * Defines the contract for managing shopping cart items.
 */
interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>

    /**
     * Adds a product to the cart. If the product already exists, its quantity should be increased.
     * @param product The product to add.
     */
    suspend fun addProduct(product: Product)

    /**
     * Removes a product from the cart.
     * @param productId The ID of the product to remove.
     */
    suspend fun removeProduct(productId: String)

    /**
     * Updates the quantity of a specific product in the cart.
     * If the new quantity is 0 or less, the item should be removed.
     * @param productId The ID of the product whose quantity is to be updated.
     * @param newQuantity The new quantity for the product.
     */
    suspend fun updateQuantity(productId: String, newQuantity: Int)

    /**
     * Clears all items from the shopping cart.
     */
    suspend fun clearCart()
}