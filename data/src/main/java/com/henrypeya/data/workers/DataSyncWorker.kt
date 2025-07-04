package com.henrypeya.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.data.local.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
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
        try {
            // --- Lógica de sincronización o mantenimiento de la BD ---
            // Aquí puedes:
            // 1. Verificar si hay órdenes nuevas y sincronizarlas con un backend (si tuvieras uno).
            // 2. Realizar limpieza de datos antiguos.
            // 3. Verificar la versión de la BD y, si fuera necesario en un escenario real,
            //    ejecutar lógica de migración compleja o notificar al usuario.

            println("DataSyncWorker: Iniciando tarea de sincronización/mantenimiento de la BD...")

            // Ejemplo: Contar órdenes (solo para demostrar el acceso a la BD)
            val totalOrders = orderRepository.getAllOrders().first().size
            println("DataSyncWorker: Total de órdenes en la BD: $totalOrders")

            // Ejemplo: Simular un error para ver el comportamiento de WorkManager
            // if (System.currentTimeMillis() % 2 == 0L) {
            //     throw Exception("Simulando un error en la sincronización")
            // }

            println("DataSyncWorker: Tarea de sincronización/mantenimiento completada exitosamente.")
            Result.success()
        } catch (e: Exception) {
            System.err.println("DataSyncWorker: Error en la tarea de sincronización: ${e.message}")
            e.printStackTrace()
            // Puedes decidir si reintentar (Result.retry()) o fallar (Result.failure())
            Result.failure()
        }
    }
}