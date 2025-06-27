package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case to get the current list of items in the shopping cart.
 * It simply delegates the call to the CartRepository.
 */
class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> {
        return cartRepository.getCartItems()
    }
}
