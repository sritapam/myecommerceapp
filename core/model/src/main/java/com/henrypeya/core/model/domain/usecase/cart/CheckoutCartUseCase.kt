package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.core.model.domain.model.order.Order as DomainOrder
import com.henrypeya.core.model.domain.model.order.OrderItem as DomainOrderItem
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject

class CheckoutCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke() {
        val currentCartItems = cartRepository.getCartItems().first()

        if (currentCartItems.isEmpty()) {
            throw Exception("El carrito está vacío. No se puede procesar la compra.")
        }

        val orderItems = currentCartItems.map { cartItem ->
            DomainOrderItem(
                product = cartItem.product,
                quantity = cartItem.quantity
            )
        }
        val total = currentCartItems.sumOf { it.calculateTotalPrice() }

        val newOrder = DomainOrder(
            id = 0,
            date = Date(),
            total = total,
            products = orderItems
        )

        orderRepository.saveOrder(newOrder)
        cartRepository.clearCart()
    }
}