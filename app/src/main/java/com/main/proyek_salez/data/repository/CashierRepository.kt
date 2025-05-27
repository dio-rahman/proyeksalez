package com.main.proyek_salez.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashierRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cartItemDao: CartItemDao,
    private val foodDao: FoodDao,
    private val orderDao: OrderDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun getAllCartItems(): Flow<List<CartItemWithFood>> {
        return cartItemDao.getCartItemsWithFood()
    }

    suspend fun addToCart(foodItem: FoodItemEntity) {
        // Simpan foodItem ke Room untuk relasi lokal
        foodDao.insertFoodItem(foodItem)
        val existingItem = cartItemDao.getCartItemByFoodId(foodItem.id)
        if (existingItem != null) {
            cartItemDao.update(existingItem.copy(quantity = existingItem.quantity + 1))
        } else {
            cartItemDao.insert(CartItemEntity(foodItemId = foodItem.id, quantity = 1))
        }
    }

    suspend fun decrementItem(foodItem: FoodItemEntity) {
        val existingItem = cartItemDao.getCartItemByFoodId(foodItem.id)
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                cartItemDao.update(existingItem.copy(quantity = existingItem.quantity - 1))
            } else {
                cartItemDao.delete(existingItem)
            }
        }
    }

    suspend fun clearCart() {
        cartItemDao.clearCart()
    }

    suspend fun createOrder(customerName: String, cartItems: List<CartItemWithFood>, paymentMethod: String) {
        if (cartItems.isNotEmpty()) {
            val totalPrice = cartItems.sumOf { it.foodItem.price.toLong() * it.cartItem.quantity.toLong() }
            val order = OrderEntity(
                customerName = customerName,
                totalPrice = totalPrice,
                orderDate = LocalDateTime.now(),
                items = cartItems.map { it.cartItem },
                paymentMethod = paymentMethod
            )
            firestore.collection("orders").add(order).await()
            clearCart()
        }
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> = callbackFlow {
        val listener = firestore.collection("food_items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(FoodItemEntity::class.java) ?: emptyList()
                // Sinkronkan ke Room di luar listener
                coroutineScope.launch {
                    items.forEach { foodDao.insertFoodItem(it) }
                }
                trySend(items).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> = callbackFlow {
        val categoryId = firestore.collection("categories")
            .whereEqualTo("name", category)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.id
        if (categoryId != null) {
            val listener = firestore.collection("food_items")
                .whereEqualTo("categoryId", categoryId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot?.toObjects(FoodItemEntity::class.java) ?: emptyList()
                    // Sinkronkan ke Room di luar listener
                    coroutineScope.launch {
                        items.forEach { foodDao.insertFoodItem(it) }
                    }
                    trySend(items).isSuccess
                }
            awaitClose { listener.remove() }
        } else {
            trySend(emptyList()).isSuccess
            close()
        }
    }

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return firestore.collection("food_items")
            .document(id.toString())
            .get()
            .await()
            .toObject(FoodItemEntity::class.java)
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = callbackFlow {
        val listener = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(OrderEntity::class.java) ?: emptyList()
                trySend(orders).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getOrderHistory(): Flow<List<OrderEntity>> = getAllOrders()

    suspend fun getAllCategories(): List<CategoryEntity> {
        return firestore.collection("categories")
            .get()
            .await()
            .toObjects(CategoryEntity::class.java)
    }

    fun getDailyOrders(date: String): Flow<List<OrderEntity>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("orderDate", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(OrderEntity::class.java) ?: emptyList()
                trySend(orders).isSuccess
            }
        awaitClose { listener.remove() }
    }

    suspend fun closeDailyOrders(date: String) {
        val orders = firestore.collection("orders")
            .whereEqualTo("orderDate", date)
            .get()
            .await()
            .toObjects(OrderEntity::class.java)
        if (orders.isNotEmpty()) {
            if (orders.any { it.totalPrice <= 0 }) {
                throw IllegalStateException("Ada pesanan dengan total harga tidak valid")
            }
            orders.forEach { order ->
                firestore.collection("orders")
                    .document(order.orderId.toString())
                    .update("status", "closed")
                    .await()
            }
            val totalRevenue = orders.sumOf { it.totalPrice }.toDouble()
            val totalMenuItems = orders.sumOf { order -> order.items.sumOf { it.quantity } }
            val totalCustomers = orders.map { it.customerName }.distinct().size
            val summary = DailySummaryEntity(
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
    }
}