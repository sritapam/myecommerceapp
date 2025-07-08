package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.model.product.Product
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(product: Product) {
        cartRepository.addProduct(product)
    }
}