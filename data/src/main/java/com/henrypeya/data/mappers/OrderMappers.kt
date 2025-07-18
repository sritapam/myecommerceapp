package com.henrypeya.data.mappers

import com.henrypeya.core.model.domain.model.order.Order as DomainOrder
import com.henrypeya.core.model.domain.model.product.Product as DomainProduct
import com.henrypeya.core.model.domain.model.order.OrderItem as DomainOrderItem
import com.henrypeya.data.local.converters.Converters
import com.henrypeya.data.local.entities.OrderEntity
import com.henrypeya.data.local.entities.OrderItemEntity
import com.henrypeya.data.remote.dto.order.OrderItemDto
import com.henrypeya.data.remote.dto.order.OrderRequestDto
import com.henrypeya.data.remote.dto.order.OrderResponseDto
import java.util.Date
import java.util.UUID

fun DomainOrder.toEntity(): OrderEntity {
    val entityProducts = this.products.map { domainOrderItem ->
        OrderItemEntity(
            productId = domainOrderItem.product.id,
            name = domainOrderItem.product.name,
            price = domainOrderItem.product.price,
            quantity = domainOrderItem.quantity,
            imageUrl = domainOrderItem.product.imageUrl,
            category = domainOrderItem.product.category
        )
    }
    return OrderEntity(
        id = this.id,
        orderIdApi = this.orderIdApi,
        date = this.date,
        total = this.total,
        productsJson = Converters().fromProductList(entityProducts) ?: "[]",
        isSynced = this.isSynced,
        category = this.category
    )
}

fun OrderEntity.toDomain(): DomainOrder {
    val entityProducts = Converters().toProductList(this.productsJson) ?: emptyList()
    val domainOrderItems = entityProducts.map { entityProduct ->
        DomainOrderItem(
            product = DomainProduct(
                id = entityProduct.productId,
                name = entityProduct.name,
                description = "N/A",
                imageUrl = entityProduct.imageUrl ?: "",
                price = entityProduct.price,
                hasDrink = false,
                category = entityProduct.category ?: "Uncategorized"
            ),
            quantity = entityProduct.quantity
        )
    }
    return DomainOrder(
        id = this.id,
        orderIdApi = this.orderIdApi,
        date = this.date,
        total = this.total,
        products = domainOrderItems,
        isSynced = this.isSynced,
        category = domainOrderItems.firstOrNull()?.product?.category ?: "Uncategorized"
    )
}

fun DomainOrder.toRequestDto(): OrderRequestDto {
    val orderItemDtos = this.products.map { domainOrderItem ->
        OrderItemDto(
            name = domainOrderItem.product.name,
            description = domainOrderItem.product.description,
            imageUrl = domainOrderItem.product.imageUrl,
            price = domainOrderItem.product.price,
            hasDrink = domainOrderItem.product.hasDrink,
            quantity = domainOrderItem.quantity,
            category = domainOrderItem.product.category
        )
    }

    val generatedOrderId = if (this.orderIdApi.isNullOrEmpty()) {
        UUID.randomUUID().toString()
    } else {
        this.orderIdApi
    }

    return OrderRequestDto(
        orderId = generatedOrderId,
        items = orderItemDtos,
        total = this.total,
        timestamp = this.date.time
    )
}

fun OrderResponseDto.toDomain(): DomainOrder {
    val domainOrderItems = this.items.map { orderItemDto ->
        DomainOrderItem(
            product = DomainProduct(
                id = "api_${orderItemDto.name}",
                name = orderItemDto.name,
                description = orderItemDto.description ?: "N/A",
                imageUrl = orderItemDto.imageUrl,
                price = orderItemDto.price,
                hasDrink = orderItemDto.hasDrink,
                category = orderItemDto.category
            ),
            quantity = orderItemDto.quantity
        )
    }
    return DomainOrder(
        id = 0,
        orderIdApi = this.id,
        date = Date(this.timestamp),
        total = this.total,
        products = domainOrderItems,
        isSynced = true,
        category = domainOrderItems.firstOrNull()?.product?.category ?: "Uncategorized"
    )
}