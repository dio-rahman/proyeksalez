package com.main.proyek_salez.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.main.proyek_salez.data.converters.Converters

@Database(
    entities = [FoodItemEntity::class, CartItemEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SalezDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao
}