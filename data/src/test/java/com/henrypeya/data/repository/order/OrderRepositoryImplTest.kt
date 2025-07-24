package com.henrypeya.data.repository.order

import androidx.work.WorkManager
import app.cash.turbine.test
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.core.model.domain.model.user.User
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.local.entities.OrderEntity
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.remote.dto.order.OrderResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryImplTest {

    private lateinit var orderDao: OrderDao
    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository
    private lateinit var workManager: WorkManager
    private lateinit var repository: OrderRepositoryImpl

    private val dispatcher = StandardTestDispatcher()

    private val testUser = User(
        id = "user123",
        fullName = "Test User",
        email = "test@example.com",
        nationality = "Testland",
        imageUrl = null
    )
    private val testOrder = Order(
        id = 1L,
        userEmail = testUser.email,
        date = Date(),
        total = 100.0,
        products = emptyList(),
        isSynced = false,
        category = "Test"
    )
    private val testOrderEntity = OrderEntity(
        id = 1L,
        userEmail = testUser.email,
        orderIdApi = "api-123",
        date = testOrder.date,
        total = testOrder.total,
        productsJson = "[]",
        isSynced = true,
        category = "Test"
    )
    private val testOrderResponseDto = OrderResponseDto(
        id = "api-123",
        userEmail = testUser.email,
        orderId = "api-123",
        items = emptyList(),
        total = 100.0,
        timestamp = testOrder.date.time
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        orderDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        workManager = mockk(relaxed = true)
        repository = OrderRepositoryImpl(orderDao, apiService, userRepository, workManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllOrders - cuando la API tiene éxito, emite órdenes remotas y actualiza la BD`() =
        runTest(dispatcher) {
            coEvery { userRepository.getUserProfile() } returns flowOf(testUser)
            coEvery { apiService.getOrdersForUser(testUser.email) } returns listOf(
                testOrderResponseDto
            )

            repository.getAllOrders().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals("api-123", result[0].orderIdApi)
                awaitComplete()
            }

            coVerify { orderDao.clearOrdersForUser(testUser.email) }
            coVerify { orderDao.insertOrder(any()) }
        }

    @Test
    fun `getAllOrders - cuando la API falla, emite las órdenes locales del usuario`() =
        runTest(dispatcher) {

            coEvery { userRepository.getUserProfile() } returns flowOf(testUser)
            coEvery { apiService.getOrdersForUser(testUser.email) } throws IOException("Sin conexión")
            coEvery { orderDao.getOrdersForUser(testUser.email) } returns flowOf(
                listOf(
                    testOrderEntity
                )
            )

            repository.getAllOrders().test {
                val result = awaitItem()
                assertEquals(1, result.size)
                assertEquals(testUser.email, result[0].userEmail)
                awaitComplete()
            }
        }

    @Test
    fun `getAllOrders - cuando no hay usuario logueado, emite una lista vacía`() =
        runTest(dispatcher) {

            coEvery { userRepository.getUserProfile() } returns flowOf(
                User(
                    id = "no_auth",
                    email = "",
                    fullName = "Invitado",
                    nationality = ""
                )
            )

            repository.getAllOrders().test {
                val result = awaitItem()
                assertTrue(result.isEmpty())
                awaitComplete()
            }

            coVerify(exactly = 0) { apiService.getOrdersForUser(any()) }
            coVerify(exactly = 0) { orderDao.getOrdersForUser(any()) }
        }

    @Test
    fun `saveOrder - cuando la API falla, guarda la orden como no sincronizada`() =
        runTest(dispatcher) {

            coEvery { apiService.createOrder(any()) } throws IOException("API falló")

            repository.saveOrder(testOrder)

            coVerify {
                orderDao.insertOrder(match {
                    !it.isSynced && it.userEmail == testUser.email
                })
            }
        }

    @Test
    fun `getUnSyncedOrders - devuelve solo las órdenes no sincronizadas del usuario actual`() =
        runTest(dispatcher) {

            coEvery { userRepository.getUserProfile() } returns flowOf(testUser)
            coEvery { orderDao.getUnSyncedOrdersForUser(testUser.email) } returns listOf(
                testOrderEntity.copy(isSynced = false)
            )

            val result = repository.getUnSyncedOrders()

            assertEquals(1, result.size)
            assertFalse(result[0].isSynced)
            assertEquals(testUser.email, result[0].userEmail)
        }

    @Test
    fun `syncOrder - si la orden no existe, la inserta como sincronizada`() = runTest(dispatcher) {

        coEvery { apiService.createOrder(any()) } returns testOrderResponseDto
        coEvery { orderDao.getOrderById(testOrder.id) } returns null // La orden no existe localmente

        val result = repository.syncOrder(testOrder)

        assertTrue(result)
        coVerify {
            orderDao.updateOrder(match { it.isSynced && it.orderIdApi == "api-123" })
        }
    }
}
