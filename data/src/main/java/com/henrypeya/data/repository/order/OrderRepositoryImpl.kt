package com.henrypeya.data.repository.order

import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.data.local.converters.Converters
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.local.entities.OrderEntity

import com.henrypeya.core.model.domain.model.order.Order as DomainOrder
import com.henrypeya.core.model.domain.model.product.Product as DomainProduct
import com.henrypeya.core.model.domain.model.order.OrderItem as DomainOrderItem
import com.henrypeya.data.local.entities.Product as EntityProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {
    override suspend fun saveOrder(order: DomainOrder) {
        val orderEntity = order.toEntity()
        orderDao.insertOrder(orderEntity)
    }

    override fun getAllOrders(): Flow<List<DomainOrder>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

fun DomainOrder.toEntity(): OrderEntity {
    val entityProducts = this.products.map { domainOrderItem: DomainOrderItem ->
        EntityProduct(
            productId = domainOrderItem.product.id,
            name = domainOrderItem.product.name,
            price = domainOrderItem.product.price,
            quantity = domainOrderItem.quantity,
            imageUrl = domainOrderItem.product.imageUrl
        )
    }
    return OrderEntity(
        id = this.id,
        date = this.date,
        total = this.total,
        productsJson = Converters().fromProductList(entityProducts) ?: "[]"
    )
}

fun OrderEntity.toDomain(): DomainOrder {
    val entityProducts = Converters().toProductList(this.productsJson) ?: emptyList()
    val domainOrderItems = entityProducts.map { entityProduct: EntityProduct ->
        DomainOrderItem(
            product = DomainProduct(
                id = entityProduct.productId,
                name = entityProduct.name,
                description = "N/A",
                price = entityProduct.price,
                includesDrink = false,
                imageUrl = entityProduct.imageUrl
            ),
            quantity = entityProduct.quantity
        )
    }
    return DomainOrder(
        id = this.id,
        date = this.date,
        total = this.total,
        products = domainOrderItems
    )
}