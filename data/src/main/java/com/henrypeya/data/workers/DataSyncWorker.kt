package com.henrypeya.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.data.local.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDatabase: AppDatabase,
    private val orderRepository: OrderRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("DataSyncWorker", "Iniciando ejecución de DataSyncWorker...")
        try {
            val needsSync = (System.currentTimeMillis() % 3 == 0L)
            val needsMigration = (System.currentTimeMillis() % 5 == 0L)

            if (needsSync) {
                Log.i("DataSyncWorker", "Simulando detección de nuevas entidades o cambios.")
                delay(2000)
                val totalOrders = orderRepository.getAllOrders().first().size
                Log.i("DataSyncWorker", "Sincronización simulada completada. Órdenes actuales: $totalOrders")
            } else {
                Log.d("DataSyncWorker", "No se detectaron nuevas entidades o cambios que requieran sincronización.")
            }

            if (needsMigration) {
                Log.w("DataSyncWorker", "Simulando detección de cambio de versión o necesidad de migración/limpieza.")
                delay(3000) // Simular un retardo de migración/limpieza
                Log.w("DataSyncWorker", "Migración/limpieza simulada completada.")
            } else {
                Log.d("DataSyncWorker", "No se detectó necesidad de migración/limpieza.")
            }

            Log.d("DataSyncWorker", "Ejecución de DataSyncWorker finalizada con éxito.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Error durante la ejecución de DataSyncWorker: ${e.localizedMessage}", e)
            e.printStackTrace()
            Result.failure()
        }
    }
}
