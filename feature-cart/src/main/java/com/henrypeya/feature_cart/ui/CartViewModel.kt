package com.henrypeya.feature_cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.model.cart.PaymentMethod
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.core.model.domain.usecase.cart.CheckoutCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.henrypeya.feature_cart.R
import com.henrypeya.feature_cart.ui.state.CartEvent
import com.henrypeya.feature_cart.ui.state.CartState
import com.henrypeya.library.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
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
    private val resources: ResourceProvider,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    private val _messageEventFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messageEventFlow = _messageEventFlow.asSharedFlow()

    private val _navigateEventFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateEventFlow = _navigateEventFlow.asSharedFlow()

    private val _eventFlow = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val eventFlow = _eventFlow.asSharedFlow()


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
                _messageEventFlow.emit(
                    resources.getString(
                        R.string.error_updating_quantity,
                        e.localizedMessage ?: resources.getString(R.string.error_unknown)
                    )
                )
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
                _messageEventFlow.emit(
                    resources.getString(
                        R.string.error_removing_item,
                        e.localizedMessage ?: resources.getString(R.string.error_unknown)
                    )
                )
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
                _messageEventFlow.emit(
                    resources.getString(
                        R.string.error_clearing_cart,
                        e.localizedMessage ?: resources.getString(R.string.error_unknown)
                    )
                )
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCheckout(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userEmail = userRepository.getUserProfile().firstOrNull()?.email
            if (userEmail.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = resources.getString(R.string.login_error_email)
                    )
                }
                return@launch // Detenemos la ejecución si no hay usuario
            }

            try {
                checkoutCartUseCase()
                _messageEventFlow.emit(resources.getString(R.string.checkout_success_message))
                _navigateEventFlow.emit(Unit)
                _eventFlow.emit(CartEvent.NavigateToOrderSuccess)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = resources.getString(
                            R.string.error_checkout,
                            e.localizedMessage ?: resources.getString(R.string.error_unknown)
                        )
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