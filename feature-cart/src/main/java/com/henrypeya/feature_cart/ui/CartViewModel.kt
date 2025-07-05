package com.henrypeya.feature_cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.usecase.cart.AddToCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.CheckoutCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.henrypeya.feature_cart.ui.state.CartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.henrypeya.core.model.domain.model.product.Product as DomainProduct


@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val checkoutCartUseCase: CheckoutCartUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCartItemsUseCase().collectLatest { items ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cartItems = items,
                        totalPrice = calculateTotalPrice(items)
                    )
                }
            }
        }
    }

    private fun calculateTotalPrice(items: List<CartItem>): Double {
        return items.sumOf { it.calculateTotalPrice() }
    }

    fun onAddToCart(product: DomainProduct) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                addToCartUseCase(product)
                _uiState.update { it.copy(errorMessage = "Producto añadido al carrito.") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al añadir al carrito: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onQuantityChange(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                updateCartItemQuantityUseCase(productId, newQuantity)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al actualizar cantidad: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRemoveItem(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                removeCartItemUseCase(productId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al eliminar producto: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onClearCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                clearCartUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al vaciar carrito: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCheckout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                checkoutCartUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Compra realizada exitosamente. ¡Gracias!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al procesar la compra: ${e.localizedMessage ?: "Desconocido"}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}