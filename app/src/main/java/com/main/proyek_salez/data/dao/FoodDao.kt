package com.main.proyek_salez.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE name = :name)")
    suspend fun categoryNameExists(name: String): Boolean

    @Insert
    suspend fun insertFoodItem(foodItem: FoodItemEntity)

    @Insert
    suspend fun insertAll(foodItems: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items")
    suspend fun getAllFoodItems(): List<FoodItemEntity>

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteFoodItem(id: Long)

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: Long): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :name || '%'")
    fun searchFoodItems(name: String): Flow<List<FoodItemEntity>>

    @Query("DELETE FROM food_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM food_items WHERE categoryId = :category")
    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)
}