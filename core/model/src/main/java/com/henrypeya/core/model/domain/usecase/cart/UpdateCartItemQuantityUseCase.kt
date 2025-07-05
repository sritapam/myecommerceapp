package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.repository.cart.CartRepository
import javax.inject.Inject

/**
 * Use Case to update the quantity of a specific item in the shopping cart.
 * Delegates the call to the CartRepository.
 */
class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: String, newQuantity: Int) {
        cartRepository.updateCartItemQuantity(productId, newQuantity)
    }
}