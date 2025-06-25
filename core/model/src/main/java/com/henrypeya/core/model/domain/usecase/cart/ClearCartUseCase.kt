package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.CartRepository
import javax.inject.Inject

/**
 * Use Case to clear all items from the shopping cart.
 * Delegates the call to the CartRepository.
 */
class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke() {
        cartRepository.clearCart()
    }
}
