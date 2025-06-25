package com.henrypeya.feature_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.CartItem
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCartItemsUseCase().collectLatest { items ->
                _uiState.value = _uiState.value.copy(
                    cartItems = items,
                    totalPrice = calculateTotalPrice(items)
                )
            }
        }
    }

    private fun calculateTotalPrice(items: List<CartItem>): Double {
        return items.sumOf { it.calculateTotalPrice() }
    }

    fun onQuantityChange(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                updateCartItemQuantityUseCase(productId, newQuantity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error updating quantity: ${e.localizedMessage}",
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onRemoveItem(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                removeCartItemUseCase(productId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error removing item: ${e.localizedMessage}",
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onClearCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                clearCartUseCase()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error clearing cart: ${e.localizedMessage}",
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun errorMessageShown() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}