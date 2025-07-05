package com.henrypeya.feature_order_history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.core.model.domain.model.order.Order as DomainOrder
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

    private val _orders = MutableStateFlow<List<DomainOrder>>(emptyList())
    val orders: StateFlow<List<DomainOrder>> = _orders.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            orderRepository.getAllOrders().collectLatest { orderList: List<DomainOrder> ->
                _orders.value = orderList
            }
        }
    }

    fun errorMessageShown() {
        _errorMessage.update { null }
    }
}
