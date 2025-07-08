package com.henrypeya.feature_cart.ui.state

import com.henrypeya.core.model.domain.model.cart.CartItem

/**
 * Represents the state of the shopping cart in the application.
 * Contains a list of cart items, total price, loading state, and any error messages.
 */
data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)