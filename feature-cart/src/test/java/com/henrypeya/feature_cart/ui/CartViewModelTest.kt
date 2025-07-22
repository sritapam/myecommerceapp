package com.henrypeya.feature_cart.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.henrypeya.core.model.domain.model.cart.CartItem
import com.henrypeya.core.model.domain.model.cart.PaymentMethod
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.core.model.domain.usecase.cart.CheckoutCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.ClearCartUseCase
import com.henrypeya.core.model.domain.usecase.cart.GetCartItemsUseCase
import com.henrypeya.core.model.domain.usecase.cart.RemoveCartItemUseCase
import com.henrypeya.core.model.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.henrypeya.library.utils.ResourceProvider
import com.henrypeya.feature_cart.R
import com.henrypeya.feature_cart.ui.state.CartEvent
import com.henrypeya.feature_cart.ui.state.CartState

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CartViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCartItemsUseCase: GetCartItemsUseCase

    @Mock
    private lateinit var updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase

    @Mock
    private lateinit var removeCartItemUseCase: RemoveCartItemUseCase

    @Mock
    private lateinit var clearCartUseCase: ClearCartUseCase

    @Mock
    private lateinit var checkoutCartUseCase: CheckoutCartUseCase

    @Mock
    private lateinit var resources: ResourceProvider

    private lateinit var viewModel: CartViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        whenever(resources.getString(any<Int>(), any())) doAnswer { invocation ->
            val stringId = invocation.getArgument<Int>(0)
            val args = invocation.getArgument<Array<Any>>(1)
            when (stringId) {
                R.string.error_updating_quantity -> "Error updating quantity: ${args[0]}"
                R.string.error_removing_item -> "Error removing item: ${args[0]}"
                R.string.error_clearing_cart -> "Error clearing cart: ${args[0]}"
                R.string.error_checkout -> "Error during checkout: ${args[0]}"
                else -> "Unknown error with args: ${args.joinToString()}"
            }
        }
        whenever(resources.getString(any<Int>())) doAnswer { invocation ->
            val stringId = invocation.getArgument<Int>(0)
            when (stringId) {
                R.string.checkout_success_message -> "Checkout successful!"
                R.string.error_unknown -> "Unknown error"
                else -> "Default string without args"
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestProduct(
        id: String,
        name: String = "Test Product $id",
        description: String = "Description for $id",
        price: Double = 100.0,
        hasDrink: Boolean = false,
        imageUrl: String? = null,
        category: String = "CategoryX"
    ): Product {
        return Product(id, name, description, price, hasDrink, imageUrl, category)
    }

    private fun createTestCartItem(
        productId: String,
        quantity: Int,
        price: Double = 100.0
    ): CartItem {
        val product = createTestProduct(productId, price = price)
        return CartItem(product, quantity)
    }

    @Test
    fun `init updates uiState with cart items and total price on successful collection`() =
        runTest {
            val productA = createTestProduct("prodA", price = 10.0)
            val productB = createTestProduct("prodB", price = 5.0)
            val testCartItems = listOf(
                CartItem(productA, 2),
                CartItem(productB, 3)
            )
            whenever(getCartItemsUseCase.invoke()).thenReturn(flowOf(testCartItems))

            viewModel = CartViewModel(
                getCartItemsUseCase,
                updateCartItemQuantityUseCase,
                removeCartItemUseCase,
                clearCartUseCase,
                checkoutCartUseCase,
                resources
            )
            advanceUntilIdle()

            assertEquals(testCartItems, viewModel.uiState.value.cartItems)
            assertEquals(35.0, viewModel.uiState.value.totalPrice, 0.001)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `init with empty cart updates uiState with empty cart and zero total price`() = runTest {
        whenever(getCartItemsUseCase.invoke()).thenReturn(flowOf(emptyList()))

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.cartItems.isEmpty())
        assertEquals(0.0, viewModel.uiState.value.totalPrice, 0.001)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `onQuantityChange sets isLoading true then false and updates cart on success`() = runTest {
        val productId = "prodC"
        val initialQuantity = 2
        val newQuantity = 5
        val itemPrice = 50.0

        val initialItems = listOf(createTestCartItem(productId, initialQuantity, itemPrice))
        val updatedItems = listOf(createTestCartItem(productId, newQuantity, itemPrice))

        val itemsFlow = MutableStateFlow(initialItems)
        whenever(getCartItemsUseCase.invoke()).thenReturn(itemsFlow)

        whenever(updateCartItemQuantityUseCase.invoke(productId, newQuantity)).thenAnswer {
            itemsFlow.value = updatedItems
            Unit
        }

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        turbineScope {
            viewModel.uiState.test {
                val stateAfterInit = awaitItem()
                assertEquals(initialItems, stateAfterInit.cartItems)
                assertFalse(stateAfterInit.isLoading)
                assertNull(stateAfterInit.errorMessage)

                viewModel.onQuantityChange(productId, newQuantity)
                runCurrent()

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
                assertEquals(initialItems, loadingState.cartItems)

                advanceUntilIdle()

                verify(updateCartItemQuantityUseCase, times(1)).invoke(productId, newQuantity)

                var stateAfterUpdate: CartState? = null
                while (true) {
                    val nextState = awaitItem()
                    if (nextState.cartItems == updatedItems) {
                        stateAfterUpdate = nextState
                        break
                    }
                }
                assertEquals(updatedItems, stateAfterUpdate!!.cartItems)
                assertEquals(250.0, stateAfterUpdate.totalPrice, 0.001)
                assertFalse(stateAfterUpdate.isLoading)
                assertNull(stateAfterUpdate.errorMessage)
            }
        }
    }

    @Test
    fun `onRemoveItem sets isLoading true then false and removes item on success`() = runTest {
        val productId = "prodE"
        val initialItems = listOf(createTestCartItem(productId, 2))
        val itemsFlow = MutableStateFlow(initialItems)
        whenever(getCartItemsUseCase.invoke()).thenReturn(itemsFlow)

        whenever(removeCartItemUseCase.invoke(productId)).thenAnswer {
            itemsFlow.value =
                itemsFlow.value.toMutableList().apply { removeIf { it.product.id == productId } }
            Unit
        }

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        turbineScope {
            viewModel.uiState.test {
                val stateAfterInit = awaitItem()
                assertEquals(initialItems, stateAfterInit.cartItems)
                assertFalse(stateAfterInit.isLoading)
                assertNull(stateAfterInit.errorMessage)

                viewModel.onRemoveItem(productId)
                runCurrent()

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)
                assertEquals(initialItems, loadingState.cartItems)

                advanceUntilIdle()

                verify(removeCartItemUseCase, times(1)).invoke(productId)

                var finalState: CartState? = null
                while (true) {
                    val nextState = awaitItem()
                    if (nextState.cartItems.isEmpty()) {
                        finalState = nextState
                        break
                    }
                }
                assertTrue(finalState!!.cartItems.isEmpty())
                assertEquals(0.0, finalState!!.totalPrice, 0.001)
                assertFalse(finalState!!.isLoading)
                assertNull(finalState!!.errorMessage)
            }
        }
    }

    @Test
    fun `onClearCart sets isLoading true then false and clears cart on success`() = runTest {
        val initialItems = listOf(createTestCartItem("prodG", 2), createTestCartItem("prodH", 1))
        val itemsFlow = MutableStateFlow(initialItems)
        whenever(getCartItemsUseCase.invoke()).thenReturn(itemsFlow)

        whenever(clearCartUseCase.invoke()).thenAnswer {
            itemsFlow.value = emptyList()
            Unit
        }

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        turbineScope {
            viewModel.uiState.test {
                val stateAfterInit = awaitItem()
                assertEquals(initialItems, stateAfterInit.cartItems)
                assertFalse(stateAfterInit.isLoading)

                viewModel.onClearCart()
                runCurrent()

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                advanceUntilIdle()

                verify(clearCartUseCase, times(1)).invoke()

                var finalState: CartState? = null
                while (true) {
                    val nextState = awaitItem()
                    if (nextState.cartItems.isEmpty()) {
                        finalState = nextState
                        break
                    }
                }
                assertTrue(finalState!!.cartItems.isEmpty())
                assertEquals(0.0, finalState.totalPrice, 0.001)
                assertFalse(finalState.isLoading)
                assertNull(finalState.errorMessage)
            }
        }
    }

    @Test
    fun `onCheckout sets isLoading true then false, emits success message and navigation event on success`() = runTest {
        val initialItems = listOf(createTestCartItem("prodJ", 1))
        whenever(getCartItemsUseCase.invoke()).thenReturn(flowOf(initialItems))
        whenever(checkoutCartUseCase.invoke()).thenReturn(Unit)

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        turbineScope {
            val uiStateCollector = viewModel.uiState.testIn(this)
            val messageEventCollector = viewModel.messageEventFlow.testIn(this)
            val navigateEventCollector = viewModel.navigateEventFlow.testIn(this)
            val eventFlowCollector = viewModel.eventFlow.testIn(this)

            val stateAfterInit = uiStateCollector.awaitItem()
            assertEquals(initialItems, stateAfterInit.cartItems)
            assertFalse(stateAfterInit.isLoading)
            assertNull(stateAfterInit.errorMessage)

            viewModel.onCheckout(PaymentMethod.CARD)
            runCurrent()

            val loadingState = uiStateCollector.awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.errorMessage)

            advanceUntilIdle()

            verify(checkoutCartUseCase, times(1)).invoke()

            val successMessage = messageEventCollector.awaitItem()
            assertEquals("Checkout successful!", successMessage)

            val navigateEvent = navigateEventCollector.awaitItem()
            assertEquals(Unit, navigateEvent)

            val cartEvent = eventFlowCollector.awaitItem()
            assertEquals(CartEvent.NavigateToOrderSuccess, cartEvent)

            val finalState = uiStateCollector.awaitItem()
            assertFalse(finalState.isLoading)
            assertNull(finalState.errorMessage)

            uiStateCollector.cancel()
            messageEventCollector.cancel()
            navigateEventCollector.cancel()
            eventFlowCollector.cancel()
        }
    }

    @Test
    fun `calculateTotalPrice returns correct total for multiple items`() = runTest {
        val product1 = createTestProduct("p1", price = 10.0)
        val product2 = createTestProduct("p2", price = 20.0)
        val product3 = createTestProduct("p3", price = 5.0)

        val cartItems = listOf(
            CartItem(product1, 2), // 2 * 10.0 = 20.0
            CartItem(product2, 1), // 1 * 20.0 = 20.0
            CartItem(product3, 4)  // 4 * 5.0 = 20.0
        )
        whenever(getCartItemsUseCase.invoke()).thenReturn(flowOf(cartItems))

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        assertEquals(60.0, viewModel.uiState.value.totalPrice, 0.001)
    }

    @Test
    fun `calculateTotalPrice returns zero for empty cart`() = runTest {
        whenever(getCartItemsUseCase.invoke()).thenReturn(flowOf(emptyList()))

        viewModel = CartViewModel(
            getCartItemsUseCase,
            updateCartItemQuantityUseCase,
            removeCartItemUseCase,
            clearCartUseCase,
            checkoutCartUseCase,
            resources
        )
        advanceUntilIdle()

        assertEquals(0.0, viewModel.uiState.value.totalPrice, 0.001)
    }

}