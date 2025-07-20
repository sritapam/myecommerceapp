package com.henrypeya.feature_order_history.ui.state

import com.henrypeya.core.model.domain.model.order.Order

data class OrderHistoryState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)