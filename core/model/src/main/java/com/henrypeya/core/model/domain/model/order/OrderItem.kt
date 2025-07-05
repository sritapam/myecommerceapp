package com.henrypeya.core.model.domain.model.order

import com.henrypeya.core.model.domain.model.product.Product

data class OrderItem(
    val product: Product,
    val quantity: Int
)