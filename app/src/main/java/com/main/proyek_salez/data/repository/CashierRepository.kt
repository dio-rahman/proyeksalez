package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashierRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val cartId = "current_cart"
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    fun updateCustomerName(name: String) {
        _customerName.value = name
        Log.d("CashierRepository", "Customer name updated to: '$name'")
    }

    fun clearCustomerName() {
        _customerName.value = ""
        Log.d("CashierRepository", "Customer name cleared")
    }

    fun getRecommendedItems(): Flow<List<FoodItemEntity>> = flow {
        try {
            val snapshot = firestore.collection("food_items")
                .orderBy("salesCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(FoodItemEntity::class.java)
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to parse recommended item: ${e.message}")
                    null
                }
            }
            emit(items)
        } catch (e: Exception) {
            Log.e("CashierRepository", "Error fetching recommended items: ${e.message}")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    fun getAllCartItems(): Flow<List<CartItemWithFood>> = callbackFlow {
        Log.d("CashierRepository", "=== SETTING UP REAL-TIME CART LISTENER ===")

        val listener = firestore.collection("carts")
            .document(cartId)
            .addSnapshotListener { cartSnapshot, error ->
                if (error != null) {
                    Log.e("CashierRepository", "Error listening to cart: ${error.message}")
                    trySend(emptyList<CartItemWithFood>())
                    return@addSnapshotListener
                }

                if (cartSnapshot?.exists() != true) {
                    Log.d("CashierRepository", "Cart document doesn't exist - emitting empty list")
                    trySend(emptyList<CartItemWithFood>())
                    return@addSnapshotListener
                }

                try {
                    val itemsData = cartSnapshot.get("items") as? List<Map<String, Any>>
                    Log.d("CashierRepository", "Real-time cart update - Raw data: $itemsData")

                    if (itemsData.isNullOrEmpty()) {
                        Log.d("CashierRepository", "Cart is empty - emitting empty list")
                        trySend(emptyList<CartItemWithFood>())
                        return@addSnapshotListener
                    }

                    val cartItems = itemsData.mapNotNull { item ->
                        try {
                            val cartItemId = (item["cartItemId"] as? Number)?.toInt() ?: 0
                            val foodItemId = when (val foodId = item["foodItemId"]) {
                                is Number -> foodId.toLong()
                                is String -> foodId.toLongOrNull() ?: 0L
                                else -> 0L
                            }
                            val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                            Log.d("CashierRepository", "Real-time parsed: foodId=$foodItemId, quantity=$quantity")
                            CartItemEntity(
                                cartItemId = cartItemId,
                                foodItemId = foodItemId,
                                quantity = quantity
                            )
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Failed to parse cart item: ${e.message}")
                            null
                        }
                    }

                    Log.d("CashierRepository", "Real-time parsed ${cartItems.size} cart items")

                    val foodItemIds = cartItems.map { it.foodItemId }.distinct()
                    Log.d("CashierRepository", "Fetching food items for IDs: $foodItemIds")

                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        try {
                            val foodItemsMap = mutableMapOf<Long, FoodItemEntity>()

                            // Fetch food items in batches
                            foodItemIds.chunked(10).forEach { batch ->
                                val foodSnapshot = firestore.collection("food_items")
                                    .whereIn("id", batch)
                                    .get()
                                    .await()

                                foodSnapshot.documents.forEach { doc ->
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
                                        Log.d("CashierRepository", "Real-time food item loaded: ${foodItem.name} (ID: $id)")
                                    } catch (e: Exception) {
                                        Log.e("CashierRepository", "Failed to parse food item: ${e.message}")
                                    }
                                }
                            }

                            val cartWithFood = cartItems.mapNotNull { cartItem ->
                                val foodItem = foodItemsMap[cartItem.foodItemId]
                                if (foodItem != null) {
                                    Log.d("CashierRepository", "Real-time cart item: ${foodItem.name} x ${cartItem.quantity}")
                                    CartItemWithFood(cartItem, foodItem)
                                } else {
                                    Log.w("CashierRepository", "Food item not found for ID: ${cartItem.foodItemId}")
                                    null
                                }
                            }

                            Log.d("CashierRepository", "Real-time emitting ${cartWithFood.size} cart items with food")
                            cartWithFood.forEach { item ->
                                Log.d("CashierRepository", "  - ${item.foodItem.name}: ${item.cartItem.quantity}")
                            }

                            trySend(cartWithFood)
                        } catch (e: Exception) {
                            Log.e("CashierRepository", "Error fetching food items in real-time: ${e.message}")
                            trySend(emptyList<CartItemWithFood>())
                        }
                    }

                } catch (e: Exception) {
                    Log.e("CashierRepository", "Error processing real-time cart snapshot: ${e.message}")
                    trySend(emptyList<CartItemWithFood>())
                }
            }

        awaitClose {
            listener.remove()
            Log.d("CashierRepository", "Real-time cart listener removed")
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addToCart(foodItem: FoodItemEntity) {
        try {
            Log.d("CashierRepository", "=== ADD TO CART: ${foodItem.name} (ID: ${foodItem.id}) ===")

            val cartRef = firestore.collection("carts").document(cartId)

            firestore.runTransaction { transaction ->
                val cartSnapshot = transaction.get(cartRef)

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

                val itemsAsMap = updatedItems.map { item ->
                    mapOf(
                        "cartItemId" to item.cartItemId,
                        "foodItemId" to item.foodItemId,
                        "quantity" to item.quantity
                    )
                }

                Log.d("CashierRepository", "Transaction: Saving cart with ${itemsAsMap.size} items")
                itemsAsMap.forEachIndexed { index, item ->
                    Log.d("CashierRepository", "Transaction Item $index: foodItemId=${item["foodItemId"]}, quantity=${item["quantity"]}")
                }

                transaction.set(cartRef, mapOf("items" to itemsAsMap))
            }.await()

            Log.d("CashierRepository", "Successfully added ${foodItem.name} to cart via transaction")
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to add to cart: ${e.message}")
            throw e
        }
    }

    suspend fun decrementItem(foodItem: FoodItemEntity) {
        try {
            Log.d("CashierRepository", "=== DECREMENT ITEM: ${foodItem.name} (ID: ${foodItem.id}) ===")

            val cartRef = firestore.collection("carts").document(cartId)

            firestore.runTransaction { transaction ->
                val cartSnapshot = transaction.get(cartRef)

                if (!cartSnapshot.exists()) {
                    Log.w("CashierRepository", "Cart doesn't exist, cannot decrement")
                    return@runTransaction
                }

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

                val existingItem = currentItems.find { it.foodItemId == foodItem.id }
                if (existingItem == null) {
                    Log.w("CashierRepository", "Item not found in cart: ${foodItem.name}")
                    return@runTransaction
                }

                Log.d("CashierRepository", "Current quantity for ${foodItem.name}: ${existingItem.quantity}")

                val updatedItems = currentItems.mapNotNull { item ->
                    if (item.foodItemId == foodItem.id) {
                        if (item.quantity > 1) {
                            Log.d("CashierRepository", "Decreasing quantity from ${item.quantity} to ${item.quantity - 1}")
                            item.copy(quantity = item.quantity - 1)
                        } else {
                            Log.d("CashierRepository", "Removing item from cart (quantity was 1)")
                            null
                        }
                    } else {
                        item
                    }
                }

                val itemsAsMap = updatedItems.map { item ->
                    mapOf(
                        "cartItemId" to item.cartItemId,
                        "foodItemId" to item.foodItemId,
                        "quantity" to item.quantity
                    )
                }

                Log.d("CashierRepository", "Transaction: Saving cart with ${itemsAsMap.size} items after decrement")
                transaction.set(cartRef, mapOf("items" to itemsAsMap))
            }.await()

            Log.d("CashierRepository", "Successfully decremented ${foodItem.name} via transaction")
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
        Log.d("CashierRepository", "=== GET DAILY ORDERS FOR DATE: $date ===")

        var listener: ListenerRegistration? = null

        try {
            val localDate = java.time.LocalDate.parse(date)
            val startOfDay = localDate.atStartOfDay()
            val endOfDay = localDate.atTime(23, 59, 59)

            val startTimestamp = Timestamp(
                java.util.Date.from(
                    startOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant()
                )
            )
            val endTimestamp = Timestamp(
                java.util.Date.from(
                    endOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant()
                )
            )

            Log.d("CashierRepository", "Querying orders from $startTimestamp to $endTimestamp")

            listener = firestore.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startTimestamp)
                .whereLessThanOrEqualTo("orderDate", endTimestamp)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("CashierRepository", "Error fetching daily orders: ${error.message}")
                        trySend(emptyList<OrderEntity>())
                        return@addSnapshotListener
                    }

                    val orders = try {
                        snapshot?.documents?.mapNotNull { doc ->
                            try {
                                val order = doc.toObject(OrderEntity::class.java)
                                order?.copy(orderId = doc.id.hashCode())
                            } catch (e: Exception) {
                                Log.e("CashierRepository", "Failed to deserialize order: ${e.message}")
                                null
                            }
                        } ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("CashierRepository", "Failed to parse orders: ${e.message}")
                        emptyList()
                    }

                    Log.d("CashierRepository", "Found ${orders.size} orders for date $date")
                    orders.forEach { order ->
                        Log.d("CashierRepository", "Order: ${order.customerName} - Rp ${order.totalPrice} - ${order.orderDate}")
                    }

                    trySend(orders)
                }

        } catch (e: Exception) {
            Log.e("CashierRepository", "Error in getDailyOrders: ${e.message}")
            trySend(emptyList<OrderEntity>())
        }

        awaitClose {
            listener?.remove()
            Log.d("CashierRepository", "Removed listener for daily orders")
        }
    }.flowOn(Dispatchers.IO)

    suspend fun closeDailyOrders(date: String) {
        try {
            Log.d("CashierRepository", "=== CLOSING DAILY ORDERS FOR DATE: $date ===")

            val localDate = java.time.LocalDate.parse(date)
            val startOfDay = localDate.atStartOfDay()
            val endOfDay = localDate.atTime(23, 59, 59)

            val startTimestamp = Timestamp(
                java.util.Date.from(
                    startOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant()
                )
            )
            val endTimestamp = Timestamp(
                java.util.Date.from(
                    endOfDay.atZone(java.time.ZoneId.systemDefault()).toInstant()
                )
            )

            Log.d("CashierRepository", "Querying orders from $startTimestamp to $endTimestamp")

            val ordersSnapshot = firestore.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startTimestamp)
                .whereLessThanOrEqualTo("orderDate", endTimestamp)
                .get()
                .await()

            val todayOpenOrders = ordersSnapshot.documents.filter { doc ->
                try {
                    val order = doc.toObject(OrderEntity::class.java)
                    order?.status == "open"
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to check order status: ${e.message}")
                    false
                }
            }

            val orders = todayOpenOrders.mapNotNull { doc ->
                try {
                    val order = doc.toObject(OrderEntity::class.java)
                    order?.copy(orderId = doc.id.hashCode())
                } catch (e: Exception) {
                    Log.e("CashierRepository", "Failed to deserialize order: ${e.message}")
                    null
                }
            }

            Log.d("CashierRepository", "Found ${orders.size} open orders to close for date $date")

            if (orders.isNotEmpty()) {
                if (orders.any { it.totalPrice <= 0 }) {
                    throw IllegalStateException("Ada pesanan dengan total harga tidak valid")
                }

                val batch = firestore.batch()
                todayOpenOrders.forEach { doc ->
                    batch.update(doc.reference, "status", "closed")
                }
                batch.commit().await()

                Log.d("CashierRepository", "Updated ${orders.size} orders to closed status")

                val totalRevenue = orders.sumOf { it.totalPrice }.toDouble()
                val totalMenuItems = orders.sumOf { order ->
                    order.items.sumOf { item ->
                        (item["quantity"] as? Number)?.toInt() ?: 0
                    }
                }
                val totalCustomers = orders.map { it.customerName }.distinct().size

                Log.d("CashierRepository", "Summary - Revenue: $totalRevenue, Items: $totalMenuItems, Customers: $totalCustomers")

                val summary = DailySummaryEntity.createWithLocalDateTime(
                    date = date,
                    totalRevenue = totalRevenue,
                    totalMenuItems = totalMenuItems,
                    totalCustomers = totalCustomers,
                    closedAt = java.time.LocalDateTime.now()
                )

                firestore.collection("daily_summaries")
                    .document(date)
                    .set(summary)
                    .await()

                Log.d("CashierRepository", "Daily summary saved for $date")
            } else {
                throw IllegalStateException("Tidak ada pesanan terbuka untuk hari ini")
            }
        } catch (e: Exception) {
            Log.e("CashierRepository", "Failed to close daily orders: ${e.message}")
            throw e
        }
    }
}