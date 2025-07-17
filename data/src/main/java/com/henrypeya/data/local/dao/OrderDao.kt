package com.henrypeya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.henrypeya.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE isSynced = 0")
    suspend fun getUnSyncedOrders(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE id = :localId")
    suspend fun getOrderById(localId: Long): OrderEntity?

    @Query("DELETE FROM orders")
    suspend fun clearAllOrders()
}