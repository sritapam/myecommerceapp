package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.repository.cart.CartRepository
import javax.inject.Inject

/**
 * Use Case to remove a specific item from the shopping cart.
 * Delegates the call to the CartRepository.
 */
class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: String) {
        cartRepository.removeCartItem(productId)
    }
}
