package com.main.proyek_salez.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.main.proyek_salez.data.Converters
import com.main.proyek_salez.data.daos.CartItemDao
import com.main.proyek_salez.data.daos.FoodItemDao
import com.main.proyek_salez.data.daos.OrderDao
import com.main.proyek_salez.data.entities.CartItemEntity
import com.main.proyek_salez.data.entities.FoodItemEntity
import com.main.proyek_salez.data.entities.OrderEntity

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