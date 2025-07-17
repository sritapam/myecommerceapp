package com.henrypeya.data.repository.order

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.henrypeya.core.model.domain.model.order.Order
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.remote.api.ApiService
import com.henrypeya.data.mappers.toDomain
import com.henrypeya.data.mappers.toEntity
import com.henrypeya.data.mappers.toRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val apiService: ApiService
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
                Log.d("OrderRepositoryImpl", "Orden local con ID ${order.id} actualizada (sincronizada) tras éxito de API.")
            } else {
                val localId = orderDao.insertOrder(entityToSave) // Insertamos la nueva orden de la API
                Log.d("OrderRepositoryImpl", "Orden nueva (${order.id}) guardada localmente (sincronizada) con ID: $localId tras éxito de API.")
            }

        } catch (e: HttpException) {
            Log.e("OrderRepositoryImpl", "Error HTTP al guardar orden ${order.id}: ${e.message}. Guardando localmente como no sincronizada.", e)
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
        } catch (e: IOException) {
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
        } catch (e: Exception) {
            val orderEntity = order.toEntity().copy(isSynced = false)
            orderDao.insertOrder(orderEntity)
            Log.d("OrderRepositoryImpl", "Orden ${order.id} guardada localmente como pendiente de sincronizar.")
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
            emit(domainOrdersFromApi) // Emite las órdenes obtenidas de la API

        } catch (e: HttpException) {
            val localOrders = orderDao.getAllOrders().first().map { it.toDomain() }
            emit(localOrders)
        } catch (e: IOException) {
            Log.e("OrderRepositoryImpl", "Error de red al obtener órdenes: ${e.localizedMessage}. Recurriendo a caché local.", e)
            val localOrders = orderDao.getAllOrders().first().map { it.toDomain() }
            emit(localOrders)
            Log.d("OrderRepositoryImpl", "Emitiendo órdenes desde caché local (fallback por error de red).")
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
                Log.d("OrderRepositoryImpl", "Orden local con ID ${order.id} sincronizada exitosamente con ID de API: ${createdOrderResponseDto.id}")
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
            Log.e("OrderRepositoryImpl", "Error HTTP al sincronizar orden ${order.id}: ${e.message}", e)
            false
        } catch (e: IOException) {
            Log.e("OrderRepositoryImpl", "Error de red al sincronizar orden ${order.id}: ${e.localizedMessage}", e)
            false
        } catch (e: Exception) {
            Log.e("OrderRepositoryImpl", "Error inesperado al sincronizar orden ${order.id}: ${e.localizedMessage}", e)
            false
        }
    }
}