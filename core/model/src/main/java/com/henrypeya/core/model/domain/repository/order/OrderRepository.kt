package com.henrypeya.core.model.domain.repository.order

import com.henrypeya.core.model.domain.model.order.Order
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Order entities.
 */
interface OrderRepository {
    suspend fun saveOrder(order: Order)
    fun getAllOrders(): Flow<List<Order>>
    suspend fun getUnSyncedOrders(): List<Order>
    suspend fun syncOrder(order: Order): Boolean
}