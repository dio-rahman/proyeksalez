package com.salez.kasir.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.salez.kasir.data.local.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items WHERE isAvailable = 1")
    fun getAllAvailableMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category = :category AND isAvailable = 1")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>

    @Query("SELECT DISTINCT category FROM menu_items WHERE isAvailable = 1")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItemEntity)

    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE itemId = :itemId")
    suspend fun updateMenuItemAvailability(itemId: String, isAvailable: Boolean)
}