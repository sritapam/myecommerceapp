package com.henrypeya.feature_cart.ui.state

sealed class CartEvent {
    data class ShowSnackbar(val message: String) : CartEvent()
    object NavigateToOrderSuccess : CartEvent()
}