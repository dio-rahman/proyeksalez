package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class ManagerRepository @Inject constructor(
    private val foodDao: FoodDao
) {
    suspend fun getAllCategories(): List<CategoryEntity> {
        return foodDao.getAllCategories()
    }

    suspend fun getAllFoodItems(): List<FoodItemEntity> {
        return foodDao.getAllFoodItems()
    }

    suspend fun addCategory(category: CategoryEntity): Result<Unit> {
        return try {
            if (foodDao.categoryNameExists(category.name)) {
                Result.Error("Kategori '${category.name}' sudah ada")
            } else {
                foodDao.insertCategory(category.copy(id = 0)) // Ensure new ID
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menambah kategori: ${e.message}")
        }
    }

    suspend fun deleteCategory(categoryId: Long): Result<Unit> {
        return try {
            // Check if category has associated food items
            val foodItems = foodDao.getAllFoodItems()
            if (foodItems.any { it.categoryId == categoryId }) {
                Result.Error("Tidak dapat menghapus kategori karena memiliki menu terkait")
            } else {
                foodDao.deleteCategory(categoryId)
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menghapus kategori: ${e.message}")
        }
    }

    suspend fun addFoodItem(foodItem: FoodItemEntity): Result<Unit> {
        return try {
            val existingItem = foodDao.getFoodItemById(foodItem.id)
            if (existingItem != null) {
                Result.Error("ID menu '${foodItem.id}' sudah digunakan")
            } else {
                foodDao.insertFoodItem(foodItem)
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menambah menu: ${e.message}")
        }
    }

    suspend fun updateFoodItem(foodItem: FoodItemEntity): Result<Unit> {
        return try {
            foodDao.insertFoodItem(foodItem) // Room updates if ID exists
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal memperbarui menu: ${e.message}")
        }
    }

    suspend fun deleteFoodItem(id: Long): Result<Unit> {
        return try {
            foodDao.deleteFoodItem(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal menghapus menu: ${e.message}")
        }
    }
}