package com.henrypeya.feature_cart.ui.state

import com.henrypeya.core.model.domain.model.cart.CartItem

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)