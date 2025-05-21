package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.dao.DailySummaryDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class ManagerRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val dailySummaryDao: DailySummaryDao,
    private val orderDao: OrderDao
) {
    suspend fun getAllCategories(): List<CategoryEntity> {
        return foodDao.getAllCategories()
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> {
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
            val foodItems = foodDao.getFoodItemsByCategoryId(categoryId)
            if (foodItems.isNotEmpty()) {
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
            foodDao.updateFoodItem(foodItem)
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

    suspend fun getLatestSummary(): DailySummaryEntity? {
        return dailySummaryDao.getLatestSummary()
    }

    suspend fun getPopularFoodItems(limit: Int = 5): List<Pair<FoodItemEntity, Int>> {

        val orders = orderDao.getAllOrders().first()

        val itemCount = mutableMapOf<Long, Int>()
        orders.forEach { order ->
            order.items.forEach { cartItem ->
                itemCount[cartItem.foodItemId] = itemCount.getOrDefault(cartItem.foodItemId, 0) + cartItem.quantity
            }
        }

        val sortedItems = itemCount.entries
            .sortedByDescending { it.value }
            .take(limit)

        return sortedItems.mapNotNull { entry ->
            foodDao.getFoodItemById(entry.key)?.let { foodItem ->
                foodItem to entry.value
            }
        }
    }
}