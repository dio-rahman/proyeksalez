package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

@Singleton
class ManagerRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllCategories(): List<CategoryEntity> {
        return firestore.collection("categories")
            .get()
            .await()
            .toObjects(CategoryEntity::class.java)
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> = flow {
        try {
            val snapshot = firestore.collection("food_items")
                .orderBy("__name__")
                .get()
                .await()
            val foodItems = snapshot.documents.mapNotNull { doc ->
                try {
                    // Manually deserialize to handle type mismatches
                    val id = doc.id.toLongOrNull() ?: 0L
                    val name = doc.getString("name") ?: ""
                    val description = doc.getString("description") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val imagePath = doc.getString("imagePath")
                    val categoryId = when (val rawCategoryId = doc.get("categoryId")) {
                        is Long -> rawCategoryId
                        is String -> rawCategoryId.toLongOrNull() ?: 0L
                        is Number -> rawCategoryId.toLong()
                        else -> 0L
                    }
                    val searchKeywords = doc.get("searchKeywords") as? List<String> ?: emptyList()

                    FoodItemEntity(
                        id = id,
                        name = name,
                        description = description,
                        price = price,
                        imagePath = imagePath,
                        categoryId = categoryId,
                        searchKeywords = searchKeywords
                    )
                } catch (e: Exception) {
                    Log.e("ManagerRepository", "Failed to deserialize document ${doc.id}: ${e.message}")
                    null
                }
            }
            Log.d("ManagerRepository", "Berhasil mengambil ${foodItems.size} item makanan")
            emit(foodItems)
        } catch (e: Exception) {
            Log.e("ManagerRepository", "Gagal mengambil food_items: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun addCategory(category: CategoryEntity): Result<Unit> {
        return try {
            val exists = firestore.collection("categories")
                .whereEqualTo("name", category.name)
                .get()
                .await()
                .documents
                .isNotEmpty()
            if (exists) {
                Result.Error("Kategori '${category.name}' sudah ada")
            } else {
                // Generate a new document ID
                val newDocRef = firestore.collection("categories").document()
                val newId = newDocRef.id.toLongOrNull() ?: System.currentTimeMillis() // Use timestamp as fallback
                val newCategory = category.copy(id = newId)
                newDocRef.set(newCategory).await()
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menambah kategori: ${e.message}")
        }
    }

    suspend fun deleteCategory(categoryId: Long): Result<Unit> {
        return try {
            val foodItems = firestore.collection("food_items")
                .whereEqualTo("categoryId", categoryId.toString())
                .get()
                .await()
            if (foodItems.documents.isNotEmpty()) {
                Result.Error("Tidak dapat menghapus kategori karena memiliki menu terkait")
            } else {
                firestore.collection("categories")
                    .document(categoryId.toString())
                    .delete()
                    .await()
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menghapus kategori: ${e.message}")
        }
    }

    suspend fun addFoodItem(foodItem: FoodItemEntity): Result<Unit> {
        return try {
            val exists = firestore.collection("food_items")
                .document(foodItem.id.toString())
                .get()
                .await()
                .exists()
            if (exists) {
                Result.Error("ID menu '${foodItem.id}' sudah digunakan")
            } else {
                firestore.collection("food_items")
                    .document(foodItem.id.toString())
                    .set(foodItem.copy(searchKeywords = foodItem.name.lowercase().split(" ")))
                    .await()
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menambah menu: ${e.message}")
        }
    }

    suspend fun updateFoodItem(foodItem: FoodItemEntity): Result<Unit> {
        return try {
            firestore.collection("food_items")
                .document(foodItem.id.toString())
                .set(foodItem.copy(searchKeywords = foodItem.name.lowercase().split(" ")))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal memperbarui menu: ${e.message}")
        }
    }

    suspend fun deleteFoodItem(id: Long): Result<Unit> {
        return try {
            firestore.collection("food_items")
                .document(id.toString())
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal menghapus menu: ${e.message}")
        }
    }

    suspend fun getLatestSummary(): DailySummaryEntity? {
        return firestore.collection("daily_summaries")
            .orderBy("closedAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .toObjects(DailySummaryEntity::class.java)
            .firstOrNull()
    }

    suspend fun getPopularFoodItems(limit: Int = 5): List<Pair<FoodItemEntity, Int>> {
        try {
            val snapshot = firestore.collection("orders")
                .get()
                .await()
            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id.toIntOrNull() ?: 0
                    val customerName = doc.getString("customerName") ?: ""
                    val totalPrice = doc.getLong("totalPrice") ?: 0L
                    val orderDateStr = doc.getString("orderDate") ?: ""
                    val orderDate = try {
                        LocalDateTime.parse(orderDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }
                    val items = (doc.get("items") as? List<Map<String, Any>>)?.mapNotNull { item ->
                        try {
                            CartItemEntity(
                                cartItemId = (item["cartItemId"] as? Number)?.toInt() ?: 0,
                                foodItemId = when (val foodId = item["foodItemId"]) {
                                    is Number -> foodId.toLong()
                                    is String -> foodId.toLongOrNull() ?: 0L
                                    else -> 0L
                                },
                                quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                            )
                        } catch (e: Exception) {
                            Log.e("ManagerRepository", "Failed to deserialize cart item in order ${doc.id}: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                    val paymentMethod = doc.getString("paymentMethod") ?: ""
                    val status = doc.getString("status") ?: "open"

                    OrderEntity(
                        orderId = id,
                        customerName = customerName,
                        totalPrice = totalPrice,
                        orderDate = orderDate,
                        items = items,
                        paymentMethod = paymentMethod,
                        status = status
                    )
                } catch (e: Exception) {
                    Log.e("ManagerRepository", "Failed to deserialize order ${doc.id}: ${e.message}")
                    null
                }
            }
            val itemCount = mutableMapOf<Long, Int>()
            orders.forEach { order ->
                order.items.forEach { item ->
                    itemCount[item.foodItemId] = itemCount.getOrDefault(item.foodItemId, 0) + item.quantity
                }
            }
            val sortedItems = itemCount.entries.sortedByDescending { it.value }.take(limit)
            return sortedItems.mapNotNull { entry ->
                firestore.collection("food_items")
                    .document(entry.key.toString())
                    .get()
                    .await()
                    .toObject(FoodItemEntity::class.java)?.let { foodItem ->
                        foodItem to entry.value
                    }
            }
        } catch (e: Exception) {
            Log.e("ManagerRepository", "Failed to load popular food items: ${e.message}", e)
            return emptyList()
        }
    }
}