package com.henrypeya.core.model.domain.repository.order

import com.henrypeya.core.model.domain.model.order.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun saveOrder(order: Order)
    fun getAllOrders(): Flow<List<Order>>
}