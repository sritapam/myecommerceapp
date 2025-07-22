package com.henrypeya.feature_order_history.ui

import app.cash.turbine.test
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class OrderHistoryViewModelTest {

 private val testDispatcher = StandardTestDispatcher()
 private lateinit var orderRepository: OrderRepository
 private lateinit var viewModel: OrderHistoryViewModel

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  orderRepository = mockk()
  viewModel = OrderHistoryViewModel(orderRepository)
 }

 @After
 fun tearDown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `loadOrders updates state with loading and orders`() = runTest {
  val fakeOrders = listOf(
   Order(
    id = 1,
    orderIdApi = "API_123",
    date = Date(),
    total = 100.0,
    products = emptyList(),
    isSynced = true,
    category = "delivery"
   ),
   Order(
    id = 2,
    orderIdApi = "API_456",
    date = Date(),
    total = 200.0,
    products = emptyList(),
    isSynced = false,
    category = "takeaway"
   )
  )
  coEvery { orderRepository.getAllOrders() } returns flowOf(fakeOrders)

  viewModel.loadOrders()
  advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals(false, state.isLoading)
   assertEquals(fakeOrders, state.orders)
   cancelAndIgnoreRemainingEvents()
  }
 }
}