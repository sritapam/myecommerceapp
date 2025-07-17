package com.henrypeya.feature_cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.usecase.cart.CheckoutCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.henrypeya.feature_cart.ui.state.CartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val checkoutCartUseCase: CheckoutCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    private val _messageEventFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messageEventFlow = _messageEventFlow.asSharedFlow()

    private val _navigateEventFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateEventFlow = _navigateEventFlow.asSharedFlow()

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

    fun onQuantityChange(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                updateCartItemQuantityUseCase(productId, newQuantity)
            } catch (e: Exception) {
                _messageEventFlow.emit("Error al actualizar cantidad: ${e.localizedMessage ?: "Desconocido"}")
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
                _messageEventFlow.emit("Error al eliminar producto: ${e.localizedMessage ?: "Desconocido"}")
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
                _messageEventFlow.emit("Error al vaciar carrito: ${e.localizedMessage ?: "Desconocido"}")
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
                _messageEventFlow.emit("Compra realizada exitosamente. ¡Gracias!")
                _navigateEventFlow.emit(Unit)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al procesar la compra: ${e.localizedMessage ?: "Desconocido"}"
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}