package com.henrypeya.data.repository.order

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.mappers.toDomain
import com.henrypeya.data.mappers.toEntity
import com.henrypeya.data.mappers.toRequestDto
import com.henrypeya.data.workers.DataSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val apiService: ApiService,
    private val workManager: WorkManager
) : OrderRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun saveOrder(order: Order) {
        try {
            val orderRequestDto = order.toRequestDto()
            val createdOrderResponseDto = apiService.createOrder(orderRequestDto)
            val existingOrderEntity = if (order.id != 0L) orderDao.getOrderById(order.id) else null

            val entityToSave = order.toEntity().copy(
                orderIdApi = createdOrderResponseDto.id,
                isSynced = true
            )

            if (existingOrderEntity != null) {
                orderDao.updateOrder(entityToSave)
            } else {
                val localId = orderDao.insertOrder(entityToSave)
            }

        } catch (e: HttpException) {
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
        } catch (e: IOException) {
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
        } catch (e: Exception) {
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun getAllOrders(): Flow<List<Order>> = flow {
        try {
            val orderDtos = apiService.getAllOrders()
            val domainOrdersFromApi = orderDtos.map { it.toDomain() }

          orderDao.clearAllOrders()
            domainOrdersFromApi.forEach { domainOrder ->
                orderDao.insertOrder(domainOrder.toEntity().copy(isSynced = true, orderIdApi = domainOrder.orderIdApi))
            }
            emit(domainOrdersFromApi)

        } catch (e: HttpException) {
            val localOrders = orderDao.getAllOrders().first().map { it.toDomain() }
            emit(localOrders)
        } catch (e: IOException) {
            val localOrders = orderDao.getAllOrders().first().map { it.toDomain() }
            emit(localOrders)
        } catch (e: Exception) {
            val localOrders = orderDao.getAllOrders().first().map { it.toDomain() }
            emit(localOrders)
        }
    }

    override suspend fun getUnSyncedOrders(): List<Order> {
        return orderDao.getUnSyncedOrders().map { it.toDomain() }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun syncOrder(order: Order): Boolean {
        return try {
            val orderRequestDto = order.toRequestDto()
            val createdOrderResponseDto = apiService.createOrder(orderRequestDto)

            val existingOrderEntity = orderDao.getOrderById(order.id)

            if (existingOrderEntity != null) {
                val updatedOrderEntity = existingOrderEntity.copy(
                    orderIdApi = createdOrderResponseDto.id,
                    isSynced = true
                )
                orderDao.updateOrder(updatedOrderEntity)
                true
            } else {
                val newSyncedEntity = order.toEntity().copy(
                    orderIdApi = createdOrderResponseDto.id,
                    isSynced = true
                )
                orderDao.insertOrder(newSyncedEntity)
                true
            }
        } catch (e: HttpException) {
            enqueueOneTimeSyncWorker()
            false
        } catch (e: IOException) {
            enqueueOneTimeSyncWorker()
            false
        } catch (e: Exception) {
            enqueueOneTimeSyncWorker()
            false
        }
    }

    private fun enqueueOneTimeSyncWorker() {
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeSyncRequest = OneTimeWorkRequest.Builder(DataSyncWorker::class.java)
            .setConstraints(syncConstraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                10,
                TimeUnit.MILLISECONDS
            )
            .addTag("order_sync_retry")
            .build()

        workManager.enqueue(oneTimeSyncRequest)
    }
}
