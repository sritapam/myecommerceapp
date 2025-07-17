package com.henrypeya.core.model.domain.model.order

import java.util.Date

data class Order(
    val id: Long = 0,
    val orderIdApi: String? = null,
    val date: Date,
    val total: Double,
    val products: List<OrderItem>,
    val isSynced: Boolean = false
)