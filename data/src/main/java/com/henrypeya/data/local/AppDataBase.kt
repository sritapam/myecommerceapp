package com.henrypeya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.henrypeya.data.local.converters.Converters
import com.henrypeya.data.local.dao.CartDao
import com.henrypeya.data.local.dao.OrderDao
import com.henrypeya.data.local.dao.UserDao
import com.henrypeya.data.local.entities.CartItemEntity
import com.henrypeya.data.local.entities.OrderEntity
import com.henrypeya.data.local.entities.UserEntity

@Database(entities = [CartItemEntity::class, OrderEntity::class, UserEntity::class],
    version = 5, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}