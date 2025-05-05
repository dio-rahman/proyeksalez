package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import java.io.File
import javax.inject.Inject

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class ManagerRepository @Inject constructor(
    private val foodDao: FoodDao
) {
    suspend fun addCategory(category: CategoryEntity): Result<Unit> {
        return if (foodDao.categoryNameExists(category.name)) {
            Result.Error("Kategori '${category.name}' sudah ada")
        } else {
            foodDao.insertCategory(category)
            Result.Success(Unit)
        }
    }

    suspend fun getAllCategories(): List<CategoryEntity> {
        return foodDao.getAllCategories()
    }

    suspend fun getAllFoodItems(): List<FoodItemEntity> {
        return foodDao.getAllFoodItems()
    }

    suspend fun deleteFoodItem(foodItem: FoodItemEntity) {
        foodItem.imagePath?.let { File(it).delete() }
        foodDao.deleteFoodItem(foodItem.id)
    }

    suspend fun addFoodItem(foodItem: FoodItemEntity): Result<Unit> {
        return if (foodDao.getFoodItemById(foodItem.id) != null) {
            Result.Error("ID menu sudah digunakan")
        } else {
            foodDao.insertFoodItem(foodItem)
            Result.Success(Unit)
        }
    }
}