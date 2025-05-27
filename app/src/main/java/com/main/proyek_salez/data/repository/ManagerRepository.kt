package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.model.CartItemEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                        // Manually create CategoryEntity with document ID
                        CategoryEntity(
                            id = doc.id,
                            name = doc.getString("name") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("ManagerRepository", "Failed to deserialize category ${doc.id}: ${e.message}")
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
                } ?: emptyList()
                Log.d("ManagerRepository", "Berhasil mengambil ${foodItems.size} item makanan")
                trySend(foodItems).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCategory(category: CategoryEntity): Result<Unit> {
        return try {
            val exists = firestore.collection("categories")
                .whereEqualTo("name", category.name.lowercase())
                .get()
                .await()
                .documents
                .isNotEmpty()
            if (exists) {
                Result.Error("Kategori '${category.name}' sudah ada")
            } else {
                val newDocRef = firestore.collection("categories").document()
                val newCategory = category.copy(id = newDocRef.id)
                newDocRef.set(newCategory).await()
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menambah kategori: ${e.message}")
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
                    val orderDate = doc.getTimestamp("orderDate") ?: com.google.firebase.Timestamp.now()
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

    // Setup initial data for Firestore
    suspend fun setupInitialData(): Result<Unit> {
        return try {
            Log.d("ManagerRepository", "Starting initial data setup...")

            // Check if categories already exist
            val existingCategories = firestore.collection("categories").get().await()
            if (existingCategories.documents.isNotEmpty()) {
                Log.d("ManagerRepository", "Categories already exist, skipping setup")
                return Result.Success(Unit)
            }

            // 1. Setup Categories
            val categories = listOf(
                CategoryEntity(name = "Makanan"),
                CategoryEntity(name = "Minuman"),
                CategoryEntity(name = "Lainnya")
            )

            categories.forEach { category ->
                val categoryRef = firestore.collection("categories").document()
                val categoryWithId = category.copy(id = categoryRef.id)
                categoryRef.set(categoryWithId).await()
                Log.d("ManagerRepository", "Added category: ${category.name} with ID: ${categoryRef.id}")
            }

            // 2. Get category IDs for food items
            val categoriesSnapshot = firestore.collection("categories").get().await()
            val categoryMap = categoriesSnapshot.documents.associate {
                it.getString("name") to it.id
            }

            // 3. Setup Sample Food Items
            val foodItems = listOf(
                // Makanan
                FoodItemEntity(
                    id = 1,
                    name = "Nasi Goreng",
                    description = "Nasi goreng spesial dengan telur",
                    price = 15000.0,
                    imagePath = "",
                    categoryId = categoryMap["Makanan"] ?: "",
                    searchKeywords = listOf("nasi", "goreng")
                ),
                FoodItemEntity(
                    id = 2,
                    name = "Mie Ayam",
                    description = "Mie ayam dengan bakso",
                    price = 12000.0,
                    imagePath = "",
                    categoryId = categoryMap["Makanan"] ?: "",
                    searchKeywords = listOf("mie", "ayam")
                ),
                FoodItemEntity(
                    id = 3,
                    name = "Ayam Goreng",
                    description = "Ayam goreng crispy",
                    price = 18000.0,
                    imagePath = "",
                    categoryId = categoryMap["Makanan"] ?: "",
                    searchKeywords = listOf("ayam", "goreng")
                ),

                // Minuman
                FoodItemEntity(
                    id = 4,
                    name = "Es Teh",
                    description = "Es teh manis segar",
                    price = 5000.0,
                    imagePath = "",
                    categoryId = categoryMap["Minuman"] ?: "",
                    searchKeywords = listOf("es", "teh")
                ),
                FoodItemEntity(
                    id = 5,
                    name = "Jus Jeruk",
                    description = "Jus jeruk segar",
                    price = 8000.0,
                    imagePath = "",
                    categoryId = categoryMap["Minuman"] ?: "",
                    searchKeywords = listOf("jus", "jeruk")
                ),
                FoodItemEntity(
                    id = 6,
                    name = "Kopi",
                    description = "Kopi hitam tubruk",
                    price = 6000.0,
                    imagePath = "",
                    categoryId = categoryMap["Minuman"] ?: "",
                    searchKeywords = listOf("kopi")
                ),

                // Lainnya
                FoodItemEntity(
                    id = 7,
                    name = "Kerupuk",
                    description = "Kerupuk udang renyah",
                    price = 3000.0,
                    imagePath = "",
                    categoryId = categoryMap["Lainnya"] ?: "",
                    searchKeywords = listOf("kerupuk")
                ),
                FoodItemEntity(
                    id = 8,
                    name = "Sambal",
                    description = "Sambal terasi pedas",
                    price = 2000.0,
                    imagePath = "",
                    categoryId = categoryMap["Lainnya"] ?: "",
                    searchKeywords = listOf("sambal")
                )
            )

            foodItems.forEach { item ->
                firestore.collection("food_items")
                    .document(item.id.toString())
                    .set(item)
                    .await()
                Log.d("ManagerRepository", "Added food item: ${item.name} to category: ${item.categoryId}")
            }

            Log.d("ManagerRepository", "Initial data setup completed successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("ManagerRepository", "Failed to setup initial data: ${e.message}")
            Result.Error("Failed to setup initial data: ${e.message}")
        }
    }

    // Debug Firestore data
    suspend fun debugFirestoreData() {
        try {
            Log.d("ManagerRepository", "=== DEBUGGING FIRESTORE DATA ===")

            // Debug Categories
            val categoriesSnapshot = firestore.collection("categories").get().await()
            Log.d("ManagerRepository", "=== CATEGORIES (${categoriesSnapshot.documents.size}) ===")
            categoriesSnapshot.documents.forEach { doc ->
                Log.d("ManagerRepository", "Category ID: ${doc.id}, Name: ${doc.getString("name")}")
            }

            // Debug Food Items
            val foodItemsSnapshot = firestore.collection("food_items").get().await()
            Log.d("ManagerRepository", "=== FOOD ITEMS (${foodItemsSnapshot.documents.size}) ===")
            foodItemsSnapshot.documents.forEach { doc ->
                Log.d("ManagerRepository", "Food ID: ${doc.id}")
                Log.d("ManagerRepository", "  Name: ${doc.getString("name")}")
                Log.d("ManagerRepository", "  CategoryId: ${doc.getString("categoryId")}")
                Log.d("ManagerRepository", "  Price: ${doc.getDouble("price")}")
            }

            // Debug Category Mapping
            Log.d("ManagerRepository", "=== CATEGORY MAPPING ===")
            categoriesSnapshot.documents.forEach { categoryDoc ->
                val categoryName = categoryDoc.getString("name")
                val categoryId = categoryDoc.id

                val itemsInCategory = foodItemsSnapshot.documents.filter {
                    it.getString("categoryId") == categoryId
                }

                Log.d("ManagerRepository", "Category '$categoryName' (ID: $categoryId) has ${itemsInCategory.size} items:")
                itemsInCategory.forEach { item ->
                    Log.d("ManagerRepository", "  - ${item.getString("name")}")
                }
            }

        } catch (e: Exception) {
            Log.e("ManagerRepository", "Error debugging Firestore: ${e.message}")
        }
    }
}