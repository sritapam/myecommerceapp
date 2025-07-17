package com.henrypeya.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val orderRepository: OrderRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("DataSyncWorker", "Iniciando ejecución de DataSyncWorker...")
        try {
            val unsyncedOrders = orderRepository.getUnSyncedOrders()
            if (unsyncedOrders.isNotEmpty()) {
                Log.i("DataSyncWorker", "Detectadas ${unsyncedOrders.size} órdenes no sincronizadas. Intentando sincronizar...")
                var allSyncedSuccessfully = true
                for (orderDomain in unsyncedOrders) {
                    val success = orderRepository.syncOrder(orderDomain)
                    if (!success) {
                        allSyncedSuccessfully = false
                        Log.w("DataSyncWorker", "Fallo al sincronizar la orden local con ID: ${orderDomain.id} (API ID: ${orderDomain.orderIdApi}). Se reintentará.")
                    }
                }
                if (allSyncedSuccessfully) {
                    Log.i("DataSyncWorker", "Todas las órdenes pendientes sincronizadas exitosamente.")
                } else {
                    Log.w("DataSyncWorker", "Algunas órdenes no pudieron sincronizarse. Se reintentará en la próxima ejecución.")
                    return@withContext Result.retry()
                }
            } else {
                Log.d("DataSyncWorker", "No hay órdenes pendientes de sincronización.")
            }

            Log.d("DataSyncWorker", "Ejecución de DataSyncWorker finalizada con éxito.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error durante la ejecución de DataSyncWorker: ${e.localizedMessage}", e)
            e.printStackTrace()
            Result.retry()
        }
    }
}