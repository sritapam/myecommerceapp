package com.henrypeya.data.repository.order

import androidx.work.WorkManager
import app.cash.turbine.test
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.core.model.domain.model.order.OrderItem
import com.henrypeya.core.model.domain.model.product.Product
import com.henrypeya.data.local.converters.Converters
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.local.entities.OrderEntity
import com.henrypeya.data.local.entities.OrderItemEntity
import com.henrypeya.data.mappers.toDomain
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.order.OrderItemDto
import com.henrypeya.data.remote.dto.order.OrderResponseDto
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryImplTest {

    private lateinit var orderDao: OrderDao
    private lateinit var apiService: ApiService
    private lateinit var workManager: WorkManager
    private lateinit var repository: OrderRepositoryImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        orderDao = mockk(relaxed = true)
        apiService = mockk()
        workManager = mockk(relaxed = true)
        repository = OrderRepositoryImpl(orderDao, apiService, workManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeProduct(id: String = "p1") = Product(
        id = id,
        name = "Test",
        description = "desc",
        price = 10.0,
        hasDrink = false,
        imageUrl = null,
        category = "Comida"
    )

    private fun fakeOrder(id: Long = 1L) = Order(
        id = id,
        orderIdApi = null,
        date = Date(),
        total = 20.0,
        products = listOf(OrderItem(product = fakeProduct(), quantity = 2)),
        isSynced = false,
        category = "Comida"
    )

    private fun fakeEntity(
        id: Long = 1L,
        isSynced: Boolean = true
    ): OrderEntity {
        val productList = listOf(
            OrderItemEntity(
                productId = "1",
                name = "Producto Test",
                price = 10.0,
                quantity = 2,
                imageUrl = null,
                category = "Comida"
            )
        )
        val json = Converters().fromProductList(productList) ?: "[]"
        return OrderEntity(
            id = id,
            orderIdApi = "api123",
            date = Date(0),
            total = 20.0,
            productsJson = json,
            isSynced = isSynced,
            category = "Comida"
        )
    }

    private fun Order.toEntityMock(): OrderEntity = fakeEntity(this.id, this.isSynced)

    @Test
    fun `saveOrder - when API succeeds, saves synced order`() = runTest(dispatcher) {
        val order = fakeOrder()
        val response = OrderResponseDto(
            id = "abc123",
            orderId = "abc123",
            timestamp = 20250724L,
            total = 20.0,
            items = listOf()
        )

        coEvery { apiService.createOrder(any()) } returns response
        coEvery { orderDao.getOrderById(order.id) } returns null
        coEvery { orderDao.insertOrder(any()) } returns 1L

        repository.saveOrder(order)

        coVerify {
            orderDao.insertOrder(match {
                it.orderIdApi == "abc123" && it.isSynced
            })
        }
    }

    @Test
    fun `saveOrder - when API fails, saves unsynced order`() = runTest(dispatcher) {
        val order = fakeOrder()
        coEvery { apiService.createOrder(any()) } throws IOException()
        coEvery { orderDao.insertOrder(any()) } returns 1L

        repository.saveOrder(order)

        coVerify {
            orderDao.insertOrder(match { !it.isSynced })
        }
    }

    @Test
    fun `getAllOrders - on API success, emits remote orders and saves to DB`() =
        runTest(dispatcher) {
            val remoteOrder = fakeOrder().copy(orderIdApi = "fromApi", isSynced = true)
            val remoteDto = remoteOrder.toEntityMock()
            val responseDto = OrderResponseDto(
                id = "fromApi",
                orderId = "fromApi",
                timestamp = remoteOrder.date.time,
                total = remoteOrder.total,
                items = listOf(
                    OrderItemDto(
                        name = "Test",
                        description = "desc",
                        price = 10.0,
                        hasDrink = false,
                        quantity = 2,
                        imageUrl = null,
                        category = "Comida"
                    )
                )
            )

            coEvery { apiService.getAllOrders() } returns listOf(responseDto)
            coEvery { orderDao.clearAllOrders() } just Runs
            coEvery { orderDao.insertOrder(any()) } returns 1L

            repository.getAllOrders().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("fromApi", result[0].orderIdApi)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getAllOrders - on API failure, emits local orders`() = runTest(dispatcher) {
        val fakeEntity = fakeEntity()
        val expectedDomainOrder = fakeEntity.toDomain()

        coEvery { apiService.getAllOrders() } throws IOException()
        coEvery { orderDao.getAllOrders() } returns flowOf(listOf(fakeEntity))

        repository.getAllOrders().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(expectedDomainOrder.total, result[0].total, 0.01)
            assertEquals(expectedDomainOrder.category, result[0].category)
            assertEquals(
                expectedDomainOrder.products.first().product.name,
                result[0].products.first().product.name
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getUnSyncedOrders returns mapped domain orders`() = runTest(dispatcher) {
        val entity = fakeEntity()
        coEvery { orderDao.getUnSyncedOrders() } returns listOf(entity)

        val result = repository.getUnSyncedOrders()

        assertEquals(1, result.size)
        assertEquals(entity.total, result[0].total, 0.01)
    }

    @Test
    fun `syncOrder - if order exists, updates as synced`() = runTest(dispatcher) {
        val order = fakeOrder()
        val response = OrderResponseDto(
            id = "abc123",
            orderId = "abc123",
            timestamp = 20250724L,
            total = 20.0,
            items = listOf()
        )

        coEvery { apiService.createOrder(any()) } returns response
        coEvery { orderDao.getOrderById(order.id) } returns fakeEntity()
        coEvery { orderDao.updateOrder(any()) } just Runs

        val result = repository.syncOrder(order)

        assertTrue(result)
        coVerify {
            orderDao.updateOrder(match { it.isSynced && it.orderIdApi == "abc123" })
        }
    }

    @Test
    fun `syncOrder - if order doesn't exist, inserts as synced`() = runTest(dispatcher) {
        val order = fakeOrder()
        val response = OrderResponseDto(
            id = "abc123",
            orderId = "abc123",
            timestamp = 20250724L,
            total = 20.0,
            items = listOf()
        )

        coEvery { apiService.createOrder(any()) } returns response
        coEvery { orderDao.getOrderById(order.id) } returns null
        coEvery { orderDao.insertOrder(any()) } returns 1L

        val result = repository.syncOrder(order)

        assertTrue(result)
        coVerify {
            orderDao.insertOrder(match { it.isSynced && it.orderIdApi == "abc123" })
        }
    }
}
