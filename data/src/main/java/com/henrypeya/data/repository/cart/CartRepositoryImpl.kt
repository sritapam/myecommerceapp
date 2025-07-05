package com.henrypeya.data.repository.cart

import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.data.local.dao.CartDao
import com.henrypeya.data.local.entities.CartItemEntity
import com.henrypeya.core.model.domain.model.product.Product as DomainProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override suspend fun addProduct(product: DomainProduct) {
        val existingItemEntity = cartDao.getAllCartItems().first().find { it.productId == product.id }
        if (existingItemEntity != null) {
            val updatedEntity = existingItemEntity.copy(quantity = existingItemEntity.quantity + 1)
            cartDao.updateCartItem(updatedEntity)
        } else {
            val newItemEntity = CartItemEntity(
                productId = product.id,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = 1
            )
            cartDao.insertCartItem(newItemEntity)
        }
    }

    override suspend fun updateCartItemQuantity(productId: String, quantity: Int) {
        val existingItemEntity = cartDao.getAllCartItems().first().find { it.productId == productId }
        existingItemEntity?.let {
            if (quantity > 0) {
                cartDao.updateCartItem(it.copy(quantity = quantity))
            } else {
                cartDao.deleteCartItem(productId) // Eliminar si la cantidad es 0
            }
        }
    }

    override suspend fun removeCartItem(productId: String) {
        cartDao.deleteCartItem(productId)
    }

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}

// --- Funciones de Extensión para Mapeo ---

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        product = DomainProduct(
            id = this.productId,
            name = this.name,
            description = "N/A",
            price = this.price,
            includesDrink = false,
            imageUrl = this.imageUrl
        ),
        quantity = this.quantity
    )
}
//insert
fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        productId = this.product.id,
        name = this.product.name,
        price = this.product.price,
        imageUrl = this.product.imageUrl,
        quantity = this.quantity
    )
}