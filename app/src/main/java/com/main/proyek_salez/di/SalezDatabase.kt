package com.main.proyek_salez.di

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.main.proyek_salez.data.converters.Converters
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity

@Database(
    entities = [FoodItemEntity::class, CartItemEntity::class, OrderEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SalezDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao
    abstract fun FoodDao(): FoodDao
}