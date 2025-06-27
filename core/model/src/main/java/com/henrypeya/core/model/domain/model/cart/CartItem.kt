package com.henrypeya.core.model.domain.model.cart

import com.henrypeya.core.model.domain.model.product.Product

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