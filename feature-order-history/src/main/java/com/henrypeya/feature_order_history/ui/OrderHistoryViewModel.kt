package com.henrypeya.feature_order_history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.feature_order_history.ui.state.OrderHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryState())
    val uiState: StateFlow<OrderHistoryState> = _uiState.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.getAllOrders().collectLatest { orderList ->
                _uiState.update {
                    it.copy(isLoading = false, orders = orderList)
                }
            }
        }
    }
}