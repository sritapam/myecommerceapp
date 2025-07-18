package com.henrypeya.data.local.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.henrypeya.data.local.entities.OrderItemEntity
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromProductList(products: List<OrderItemEntity>?): String? {
        if (products == null) {
            return null
        }
        val type = object : TypeToken<List<OrderItemEntity>>() {}.type
        return Gson().toJson(products, type)
    }

    @TypeConverter
    fun toProductList(productsJson: String?): List<OrderItemEntity>? {
        if (productsJson == null) {
            return null
        }
        val type = object : TypeToken<List<OrderItemEntity>>() {}.type
        return Gson().fromJson(productsJson, type)
    }
}
