package com.main.proyek_salez.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.main.proyek_salez.data.entities.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert
    suspend fun insertAll(foodItems: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items WHERE category = :category")
    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :name || '%'")
    fun searchFoodItems(name: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: Int): FoodItemEntity?
}