package com.henrypeya.core.model.domain.usecase.cart

import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.core.model.domain.model.order.Order as DomainOrder
import com.henrypeya.core.model.domain.model.order.OrderItem as DomainOrderItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject

class CheckoutCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val currentCartItems = cartRepository.getCartItems().first()

        if (currentCartItems.isEmpty()) {
            throw Exception("El carrito está vacío. No se puede procesar la compra.")
        }

        val userEmail = userRepository.getUserProfile().firstOrNull()?.email
        if (userEmail.isNullOrEmpty()) {
            throw IllegalStateException("Usuario no autenticado. No se puede procesar la compra.")
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
            userEmail = userEmail,
            date = Date(),
            total = total,
            products = orderItems,
            isSynced = false,
            category = orderItems.firstOrNull()?.product?.category ?: "Uncategorized"
        )

        orderRepository.saveOrder(newOrder)
        cartRepository.clearCart()
    }
}