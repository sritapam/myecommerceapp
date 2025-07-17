package com.henrypeya.data.mappers

import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.data.local.entities.ProductEntity

fun ProductEntity.toDomainProduct(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        hasDrink = this.hasDrink,
        imageUrl = this.imageUrl
    )
}

fun Product.toEntityProduct(): ProductEntity {
    return ProductEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        hasDrink = this.hasDrink,
        imageUrl = this.imageUrl
    )
}