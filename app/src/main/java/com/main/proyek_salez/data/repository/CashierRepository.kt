package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CashierRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val cartId = "current_cart"

    fun getAllCartItems(): Flow<List<CartItemWithFood>> = flow {
        try {
            Log.d("CashierRepository", "=== GET ALL CART ITEMS ===")

            // Get cart data
            val cartSnapshot = firestore.collection("carts")
                .document(cartId)
                .get()
                .await()

            if (!cartSnapshot.exists()) {
                Log.d("CashierRepository", "Cart document doesn't exist")
                emit(emptyList<CartItemWithFood>())
                return@flow
            }

            val cartItems = try {
                val itemsData = cartSnapshot.get("items") as? List<Map<String, Any>>
                Log.d("CashierRepository", "Raw cart data: $itemsData")

                itemsData?.mapNotNull { item ->
                    try {
                        val cartItemId = (item["cartItemId"] as? Number)?.toInt() ?: 0
                        val foodItemId = when (val foodId = item["foodItemId"]) {
                            is Number -> foodId.toLong()
                            is String -> foodId.toLongOrNull() ?: 0L
                            else -> 0L
                        }
                        val quantity = (item["quantity"] as? Number)?.toInt() ?: 0

                        Log.d("CashierRepository", "Cart item: cartItemId=$cartItemId, foodItemId=$foodItemId, quantity=$quantity")

                        CartItemEntity(
                            cartItemId = cartItemId,
                            foodItemId = foodItemId,
                            quantity = quantity
                        )
                    } catch (e: Exception) {
                        Log.e("CashierRepository", "Failed to parse cart item: ${e.message}")
                        null
                    }
                } ?: emptyList()
            } catch (e: Exception) {
                Log.e("CashierRepository", "Failed to parse cart items: ${e.message}")
                emptyList()
            }

            Log.d("CashierRepository", "Parsed ${cartItems.size} cart items")

            if (cartItems.isEmpty()) {
                emit(emptyList<CartItemWithFood>())
                return@flow
            }

            // Get food items for each cart item
            val foodItemIds = cartItems.map { it.foodItemId }.distinct()
            Log.d("CashierRepository", "Getting food items for IDs: $foodItemIds")

            val foodItemsMap = mutableMapOf<Long, FoodItemEntity>()

            // Get food items in batches of 10 (Firestore limit)
            foodItemIds.chunked(10).forEach { batch ->
                val foodItemsSnapshot = firestore.collection("food_items")
                    .whereIn("id", batch)
                    .get()
                    .await()

                foodItemsSnapshot.documents.forEach { doc ->
                    try {
                        val id = doc.id.toLongOrNull() ?: 0L
                        val name = doc.getString("name") ?: ""
                        val description = doc.getString("description") ?: ""
                        val price = doc.getDouble("price") ?: 0.0
                        val imagePath = doc.getString("imagePath") ?: ""
                        val categoryId = doc.getString("categoryId") ?: ""
                        val searchKeywords = doc.get("searchKeywords") as? List<String> ?: emptyList()

                        val foodItem = FoodItemEntity(
                            id = id,
                            name = name,
                            description = description,
                            price = price,
                            imagePath = imagePath,
                            categoryId = categoryId,
                            searchKeywords = searchKeywords
                        )

                        foodItemsMap[id] = foodItem
                        Log.d("CashierRepository", "Food item loaded: ${foodItem.name} (ID: $id)")
                    } catch (e: Exception) {
                        Log.e("CashierRepository", "Failed to parse food item: ${e.message}")
                    }
                }
            }

            // Combine cart items with food items
            val cartWithFood = cartItems.mapNotNull { cartItem ->
                val foodItem = foodItemsMap[cartItem.foodItemId]
                if (foodItem != null) {
                    Log.d("CashierRepository", "Cart item with food: ${foodItem.name} x ${cartItem.quantity}")
                    CartItemWithFood(cartItem, foodItem)
                } else {
                    Log.w("CashierRepository", "Food item not found for ID: ${cartItem.foodItemId}")
                    null
                }
            }

            Log.d("CashierRepository", "Final result: ${cartWithFood.size} cart items with food")
            emit(cartWithFood)

        } catch (e: Exception) {
            Log.e("CashierRepository", "Error getting cart items: ${e.message}")
            emit(emptyList<CartItemWithFood>())
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchFoodItemsMap(foodItemIds: List<Long>): Map<Long, FoodItemEntity> {
        if (foodItemIds.isEmpty()) return emptyMap()
        return try {
            // Split into chunks of 10 due to Firestore whereIn limit
            val batches = foodItemIds.chunked(10)
            val allFoodItems = mutableMapOf<Long, FoodItemEntity>()

            for (batch in batches) {
                val foodItemDocs = firestore.collection("food_items")
                    .whereIn("id", batch)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        try {
                            doc.toObject(FoodItemEntity::class.java)
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to deserialize food item: ${e.message}")
                            null
                        }
                    }
                    .associateBy { it.id }

                allFoodItems.putAll(foodItemDocs)
            }
            allFoodItems
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to fetch food items: ${e.message}")
            emptyMap()
        }
    }

    suspend fun addToCart(foodItem: FoodItemEntity) {
        try {
            Log.d("CashierRepository", "=== ADD TO CART: ${foodItem.name} ===")

            val cartSnapshot = firestore.collection("carts")
                .document(cartId)
                .get()
                .await()

            val currentItems = if (cartSnapshot.exists()) {
                val itemsData = cartSnapshot.get("items") as? List<Map<String, Any>> ?: emptyList()
                Log.d("CashierRepository", "Current cart has ${itemsData.size} items")

                itemsData.mapNotNull { item ->
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
                        Log.e("CashierRepository", "Failed to deserialize cart item: ${e.message}")
                        null
                    }
                }
            } else {
                Log.d("CashierRepository", "Cart document doesn't exist, creating new cart")
                emptyList()
            }

            // Check if item already exists in cart
            val existingItem = currentItems.find { it.foodItemId == foodItem.id }
            val updatedItems = if (existingItem != null) {
                Log.d("CashierRepository", "Item already in cart, updating quantity from ${existingItem.quantity} to ${existingItem.quantity + 1}")
                currentItems.map { item ->
                    if (item.foodItemId == foodItem.id) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            } else {
                val newCartItemId = (currentItems.maxOfOrNull { it.cartItemId } ?: 0) + 1
                val newItem = CartItemEntity(
                    cartItemId = newCartItemId,
                    foodItemId = foodItem.id,
                    quantity = 1
                )
                Log.d("CashierRepository", "Adding new item to cart: ${foodItem.name} with cartItemId: $newCartItemId")
                currentItems + newItem
            }

            // Convert to Map for Firestore
            val itemsAsMap = updatedItems.map { item ->
                mapOf(
                    "cartItemId" to item.cartItemId,
                    "foodItemId" to item.foodItemId,
                    "quantity" to item.quantity
                )
            }

            Log.d("CashierRepository", "Saving cart with ${itemsAsMap.size} items")
            itemsAsMap.forEachIndexed { index, item ->
                Log.d("CashierRepository", "Item $index: foodItemId=${item["foodItemId"]}, quantity=${item["quantity"]}")
            }

            firestore.collection("carts")
                .document(cartId)
                .set(mapOf("items" to itemsAsMap))
                .await()

            Log.d("CashierRepository", "Successfully added ${foodItem.name} to cart")

        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to add to cart: ${e.message}")
            throw e
        }
    }

    suspend fun decrementItem(foodItem: FoodItemEntity) {
        try {
            val cartSnapshot = firestore.collection("carts")
                .document(cartId)
                .get()
                .await()

            if (!cartSnapshot.exists()) return

            val currentItems = (cartSnapshot.get("items") as? List<Map<String, Any>>)?.mapNotNull { item ->
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
                    Log.e("CashierRepository", "Failed to deserialize cart item: ${e.message}")
                    null
                }
            } ?: emptyList()

            val updatedItems = currentItems.mapNotNull { item ->
                if (item.foodItemId == foodItem.id) {
                    if (item.quantity > 1) {
                        item.copy(quantity = item.quantity - 1)
                    } else {
                        null // Remove item if quantity becomes 0
                    }
                } else {
                    item
                }
            }

            // Convert to Map for Firestore
            val itemsAsMap = updatedItems.map { item ->
                mapOf(
                    "cartItemId" to item.cartItemId,
                    "foodItemId" to item.foodItemId,
                    "quantity" to item.quantity
                )
            }

            firestore.collection("carts")
                .document(cartId)
                .set(mapOf("items" to itemsAsMap))
                .await()
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to decrement item: ${e.message}")
            throw e
        }
    }

    suspend fun clearCart() {
        try {
            firestore.collection("carts")
                .document(cartId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to clear cart: ${e.message}")
            throw e
        }
    }

    suspend fun createOrder(customerName: String, cartItems: List<CartItemWithFood>, paymentMethod: String) {
        try {
            if (cartItems.isNotEmpty()) {
                val totalPrice = cartItems.sumOf { it.foodItem.price.toLong() * it.cartItem.quantity.toLong() }

                // Convert cart items to Map format for Firestore
                val itemsAsMap = cartItems.map { cartWithFood ->
                    mapOf(
                        "cartItemId" to cartWithFood.cartItem.cartItemId,
                        "foodItemId" to cartWithFood.cartItem.foodItemId,
                        "quantity" to cartWithFood.cartItem.quantity
                    )
                }

                val order = OrderEntity(
                    customerName = customerName,
                    totalPrice = totalPrice,
                    orderDate = Timestamp.now(),
                    items = itemsAsMap,
                    paymentMethod = paymentMethod
                )

                firestore.collection("orders").add(order).await()
                clearCart()
            }
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to create order: ${e.message}")
            throw e
        }
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> = callbackFlow {
        val listener = firestore.collection("food_items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CashierRepository", "Error fetching food items: ${error.message}")
                    trySend(emptyList<FoodItemEntity>())
                    return@addSnapshotListener
                }

                val items = try {
                    snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(FoodItemEntity::class.java)
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to deserialize food item: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse food items: ${e.message}")
                    emptyList()
                }

                trySend(items)
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> = flow {
        try {
            Log.d("CashierRepository", "=== START getFoodItemsByCategory: $category ===")

            // Step 1: Get category ID
            val categoriesSnapshot = firestore.collection("categories")
                .whereEqualTo("name", category)
                .get()
                .await()

            val categoryDoc = categoriesSnapshot.documents.firstOrNull()
            if (categoryDoc == null) {
                Log.e("CashierRepository", "Category '$category' not found")
                emit(emptyList<FoodItemEntity>())
                return@flow
            }

            val categoryId = categoryDoc.id
            Log.d("CashierRepository", "Found categoryId: $categoryId for category: $category")

            // Step 2: Get food items
            val foodItemsSnapshot = firestore.collection("food_items")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            Log.d("CashierRepository", "Found ${foodItemsSnapshot.documents.size} documents for categoryId: $categoryId")

            val foodItems = foodItemsSnapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id.toLongOrNull() ?: 0L
                    val name = doc.getString("name") ?: ""
                    val description = doc.getString("description") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val imagePath = doc.getString("imagePath") ?: ""
                    val docCategoryId = doc.getString("categoryId") ?: ""
                    val searchKeywords = doc.get("searchKeywords") as? List<String> ?: emptyList()

                    Log.d("CashierRepository", "Processing: $name (ID: $id, Price: $price)")

                    FoodItemEntity(
                        id = id,
                        name = name,
                        description = description,
                        price = price,
                        imagePath = imagePath,
                        categoryId = docCategoryId,
                        searchKeywords = searchKeywords
                    )
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse food item ${doc.id}: ${e.message}")
                    null
                }
            }

            Log.d("CashierRepository", "=== RESULT: ${foodItems.size} items for '$category' ===")
            foodItems.forEach { item ->
                Log.d("CashierRepository", "  - ${item.name} (Rp ${item.price})")
            }

            emit(foodItems)

        } catch (e: Exception) {
            Log.e("CashierRepository", "Error in getFoodItemsByCategory: ${e.message}")
            emit(emptyList<FoodItemEntity>())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return try {
            val querySnapshot = firestore.collection("food_items")
                .whereEqualTo("id", id)
                .get()
                .await()

            querySnapshot.documents.firstOrNull()?.toObject(FoodItemEntity::class.java)
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to fetch food item by ID: ${e.message}")
            null
        }
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = callbackFlow {
        val listener = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CashierRepository", "Error fetching orders: ${error.message}")
                    trySend(emptyList<OrderEntity>())
                    return@addSnapshotListener
                }

                val orders = try {
                    snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(OrderEntity::class.java)
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to deserialize order: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse orders: ${e.message}")
                    emptyList()
                }

                trySend(orders)
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getOrderHistory(): Flow<List<OrderEntity>> = getAllOrders()

    fun getAllCategories(): Flow<List<CategoryEntity>> = callbackFlow {
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CashierRepository", "Error fetching categories: ${error.message}")
                    trySend(emptyList<CategoryEntity>())
                    return@addSnapshotListener
                }

                val categories = try {
                    snapshot?.documents?.mapNotNull { doc ->
                        try {
                            CategoryEntity(
                                id = doc.id,
                                name = doc.getString("name") ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to deserialize category: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse categories: ${e.message}")
                    emptyList()
                }

                Log.d("CashierRepository", "Categories loaded: ${categories.size}")
                categories.forEach { category ->
                    Log.d("CashierRepository", "Category: ${category.name} (ID: ${category.id})")
                }

                trySend(categories)
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getDailyOrders(date: String): Flow<List<OrderEntity>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("orderDate", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CashierRepository", "Error fetching daily orders: ${error.message}")
                    trySend(emptyList<OrderEntity>())
                    return@addSnapshotListener
                }

                val orders = try {
                    snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(OrderEntity::class.java)
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to deserialize order: ${e.message}")
                            null
                        }
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse orders: ${e.message}")
                    emptyList()
                }

                trySend(orders)
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun closeDailyOrders(date: String) {
        try {
            val orders = firestore.collection("orders")
                .whereEqualTo("orderDate", date)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    try {
                        doc.toObject(OrderEntity::class.java)?.copy(orderId = doc.id.hashCode())
                    } catch (e: Exception) {
                        Log.e("CashierRepository", "Failed to deserialize order: ${e.message}")
                        null
                    }
                }

            if (orders.isNotEmpty()) {
                if (orders.any { it.totalPrice <= 0 }) {
                    throw IllegalStateException("Ada pesanan dengan total harga tidak valid")
                }

                // Update order status
                val batch = firestore.batch()
                orders.forEach { order ->
                    val orderRef = firestore.collection("orders").document(order.orderId.toString())
                    batch.update(orderRef, "status", "closed")
                }
                batch.commit().await()

                // Calculate summary
                val totalRevenue = orders.sumOf { it.totalPrice }.toDouble()
                val totalMenuItems = orders.sumOf { order ->
                    order.items.sumOf {
                        (it["quantity"] as? Number)?.toInt() ?: 0
                    }
                }
                val totalCustomers = orders.map { it.customerName }.distinct().size

                val summary = DailySummaryEntity.createWithLocalDateTime(
                    date = date,
                    totalRevenue = totalRevenue,
                    totalMenuItems = totalMenuItems,
                    totalCustomers = totalCustomers,
                    closedAt = LocalDateTime.now()
                )

                firestore.collection("daily_summaries")
                    .document(date)
                    .set(summary)
                    .await()
            } else {
                throw IllegalStateException("Tidak ada pesanan untuk hari ini")
            }
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to close daily orders: ${e.message}")
            throw e
        }
    }
    // Add this debug function to CashierRepository.kt

    suspend fun debugFirestoreData() {
        try {
            Log.d("CashierRepository", "=== DEBUGGING FIRESTORE DATA ===")

            // Debug Categories
            val categoriesSnapshot = firestore.collection("categories").get().await()
            Log.d("CashierRepository", "=== CATEGORIES (${categoriesSnapshot.documents.size}) ===")
            categoriesSnapshot.documents.forEach { doc ->
                Log.d("CashierRepository", "Category ID: ${doc.id}, Name: '${doc.getString("name")}'")
            }

            // Debug Food Items
            val foodItemsSnapshot = firestore.collection("food_items").get().await()
            Log.d("CashierRepository", "=== FOOD ITEMS (${foodItemsSnapshot.documents.size}) ===")
            foodItemsSnapshot.documents.forEach { doc ->
                Log.d("CashierRepository", "Food ID: ${doc.id}")
                Log.d("CashierRepository", "  Name: '${doc.getString("name")}'")
                Log.d("CashierRepository", "  CategoryId: '${doc.getString("categoryId")}'")
                Log.d("CashierRepository", "  Price: ${doc.getDouble("price")}")
            }

            // Debug Category Mapping
            Log.d("CashierRepository", "=== CATEGORY MAPPING ===")
            categoriesSnapshot.documents.forEach { categoryDoc ->
                val categoryName = categoryDoc.getString("name")
                val categoryId = categoryDoc.id

                val itemsInCategory = foodItemsSnapshot.documents.filter {
                    it.getString("categoryId") == categoryId
                }

                Log.d("CashierRepository", "Category '$categoryName' (ID: $categoryId) has ${itemsInCategory.size} items:")
                itemsInCategory.forEach { item ->
                    Log.d("CashierRepository", "  - ${item.getString("name")}")
                }
            }

            // Test specific category queries
            val testCategories = listOf("Makanan", "Minuman", "Lainnya")
            testCategories.forEach { categoryName ->
                Log.d("CashierRepository", "=== Testing query for '$categoryName' ===")
                val categoryQuery = firestore.collection("categories")
                    .whereEqualTo("name", categoryName)
                    .get()
                    .await()

                if (categoryQuery.documents.isNotEmpty()) {
                    val foundCategoryId = categoryQuery.documents.first().id
                    Log.d("CashierRepository", "Found category '$categoryName' with ID: $foundCategoryId")

                    val foodQuery = firestore.collection("food_items")
                        .whereEqualTo("categoryId", foundCategoryId)
                        .get()
                        .await()

                    Log.d("CashierRepository", "Found ${foodQuery.documents.size} food items for '$categoryName'")
                } else {
                    Log.d("CashierRepository", "No category found for '$categoryName'")
                }
            }

        } catch (e: Exception) {
            Log.e("CashierRepository", "Error debugging Firestore: ${e.message}")
        }
    }
    suspend fun debugCartData() {
        try {
            Log.d("CashierRepository", "=== DEBUG CART DATA ===")

            val cartSnapshot = firestore.collection("carts")
                .document(cartId)
                .get()
                .await()

            if (cartSnapshot.exists()) {
                val items = cartSnapshot.get("items")
                Log.d("CashierRepository", "Cart document exists")
                Log.d("CashierRepository", "Raw items data: $items")

                if (items is List<*>) {
                    Log.d("CashierRepository", "Items is a list with ${items.size} elements")
                    items.forEachIndexed { index, item ->
                        Log.d("CashierRepository", "Item $index: $item")
                        if (item is Map<*, *>) {
                            Log.d("CashierRepository", "  - cartItemId: ${item["cartItemId"]}")
                            Log.d("CashierRepository", "  - foodItemId: ${item["foodItemId"]}")
                            Log.d("CashierRepository", "  - quantity: ${item["quantity"]}")
                        }
                    }
                } else {
                    Log.d("CashierRepository", "Items is not a list: ${items?.javaClass?.simpleName}")
                }
            } else {
                Log.d("CashierRepository", "Cart document does not exist")
            }

        } catch (e: Exception) {
            Log.e("CashierRepository", "Error debugging cart: ${e.message}")
        }
    }
}