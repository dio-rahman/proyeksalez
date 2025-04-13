package com.main.proyek_salez.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodItems: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items WHERE category = :category")
    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :name || '%'")
    fun searchFoodItems(name: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: Int): FoodItemEntity?

    @Query("DELETE FROM food_items")
    suspend fun deleteAll()
}