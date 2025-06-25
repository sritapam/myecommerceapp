package com.henrypeya.core.model

data class CartItem(
    val product: Product,
    var quantity: Int
){
    /**
     * Calculates the total price for this cart item (product price * quantity).
     * @return The total price as a Double.
     */
    fun calculateTotalPrice(): Double {
        return product.price * quantity
    }
}