package com.main.proyek_salez.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.main.proyek_salez.data.local.OrderEntity
import com.main.proyek_salez.data.local.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt, completedAt = :completedAt WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long, completedAt: Long?)

    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    suspend fun getOrdersByDateRange(startDate: Long, endDate: Long): List<OrderEntity>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsForOrder(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM orders WHERE createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY createdAt DESC")
    fun getTodayOrders(startOfDay: Long, endOfDay: Long): Flow<List<OrderEntity>>

    // Transaction to insert order and its items
    @Transaction
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }
}
