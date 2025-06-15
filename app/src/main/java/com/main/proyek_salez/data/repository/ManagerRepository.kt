package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
    fun getAllCategories(): Flow<List<CategoryEntity>> = callbackFlow {
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ManagerRepository", "Error getting categories: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CategoryEntity(
                            id = doc.id,
                            name = doc.getString("name") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "ManagerRepository",
                            "Failed to deserialize category ${doc.id}: ${e.message}"
                        )
                        null
                    }
                } ?: emptyList()

                Log.d("ManagerRepository", "Categories loaded: ${categories.size}")
                categories.forEach { category ->
                    Log.d("ManagerRepository", "Category: ${category.name} (ID: ${category.id})")
                }

                trySend(categories).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> = callbackFlow {
        val listener = firestore.collection("food_items")
            .orderBy("__name__")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val foodItems = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val id = doc.id.toLongOrNull() ?: 0L
                        val name = doc.getString("name") ?: ""
                        val description = doc.getString("description") ?: ""
                        val price = doc.getDouble("price") ?: 0.0
                        val imagePath = doc.getString("imagePath")
                        val categoryId = doc.getString("categoryId") ?: ""
                        val searchKeywords =
                            doc.get("searchKeywords") as? List<String> ?: emptyList()

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
                        Log.e(
                            "ManagerRepository",
                            "Failed to deserialize document ${doc.id}: ${e.message}"
                        )
                        null
                    }
                } ?: emptyList()
                Log.d("ManagerRepository", "Berhasil mengambil ${foodItems.size} item makanan")
                trySend(foodItems).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCategory(category: CategoryEntity): Result<Unit> {
        return try {
            val normalizedName = category.name.lowercase().trim()
            val querySnapshot = firestore.collection("categories")
                .whereEqualTo("name", normalizedName)
                .get()
                .await()
            querySnapshot.documents.forEach { doc ->
                val name = doc.getString("name") ?: "null"
                Log.d("ManagerRepository", "Found document: ${doc.id}, name: $name")
            }
            if (!querySnapshot.isEmpty) {
                Log.d("ManagerRepository", "Category '$normalizedName' already exists")
                return Result.Error("Kategori '$normalizedName' sudah ada")
            }
            firestore.runTransaction { transaction ->
                val newDocRef = firestore.collection("categories").document()
                val newCategory = category.copy(id = newDocRef.id, name = normalizedName)
                transaction.set(newDocRef, newCategory)
                Log.d("ManagerRepository", "Added new category: ${newDocRef.id}, name: ${newCategory.name}")
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal menambah kategori: ${e.message}")
        }
    }

    suspend fun updateCategory(categoryId: String, newName: String): Result<Unit> {
        return try {
            val normalizedName = newName.lowercase().trim()
            val querySnapshot = firestore.collection("categories")
                .whereEqualTo("name", normalizedName)
                .get()
                .await()
            if (!querySnapshot.isEmpty && querySnapshot.documents.none { it.id == categoryId }) {
                return Result.Error("Kategori '$normalizedName' sudah ada")
            }
            firestore.collection("categories")
                .document(categoryId)
                .update("name", normalizedName)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Gagal memperbarui kategori: ${e.message}")
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val foodItems = firestore.collection("food_items")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()
            if (foodItems.documents.isNotEmpty()) {
                Result.Error("Tidak dapat menghapus kategori karena memiliki menu terkait")
            } else {
                firestore.collection("categories")
                    .document(categoryId)
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
            val newId = System.currentTimeMillis()
            val foodItemWithId = foodItem.copy(id = newId)
            firestore.collection("food_items")
                .document(newId.toString())
                .set(foodItemWithId.copy(searchKeywords = foodItem.name.lowercase().split(" ")))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ManagerRepository", "Error adding food item: ${e.message}")
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

    suspend fun getLatestSummary(): Pair<DailySummaryEntity?, DailySummaryEntity?> {
        return try {
            val snapshots = firestore.collection("daily_summaries")
                .orderBy("closedAt", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .await()
            val summaries = snapshots.toObjects(DailySummaryEntity::class.java)
            val latest = summaries.getOrNull(0)
            val previous = summaries.getOrNull(1)
            latest to previous
        } catch (e: Exception) {
            Log.e("ManagerRepository", "Failed to load summaries: ${e.message}", e)
            null to null
        }
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
                    val orderDate =
                        doc.getTimestamp("orderDate") ?: com.google.firebase.Timestamp.now()
                    val items = (doc.get("items") as? List<Map<String, Any>>)?.mapNotNull { item ->
                        try {
                            mapOf(
                                "cartItemId" to ((item["cartItemId"] as? Number)?.toInt() ?: 0),
                                "foodItemId" to when (val foodId = item["foodItemId"]) {
                                    is Number -> foodId.toLong()
                                    is String -> foodId.toLongOrNull() ?: 0L
                                    else -> 0L
                                },
                                "quantity" to ((item["quantity"] as? Number)?.toInt() ?: 0)
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "ManagerRepository",
                                "Failed to deserialize cart item in order ${doc.id}: ${e.message}"
                            )
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
                    Log.e(
                        "ManagerRepository",
                        "Failed to deserialize order ${doc.id}: ${e.message}"
                    )
                    null
                }
            }
            val itemCount = mutableMapOf<Long, Int>()
            orders.forEach { order ->
                order.items.forEach { item ->
                    val foodItemId = (item["foodItemId"] as? Number)?.toLong() ?: 0L
                    val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                    itemCount[foodItemId] = itemCount.getOrDefault(foodItemId, 0) + quantity
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