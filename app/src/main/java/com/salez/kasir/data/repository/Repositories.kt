package com.salez.kasir.data.repository

import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.data.models.Order
import com.salez.kasir.data.models.OrderStatus
import kotlinx.coroutines.flow.Flow


interface MenuRepository1 {

    fun getAllCategories(): Flow<List<String>>

    fun getAllMenuItems(): Flow<List<MenuItem>>

    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>>
}

interface OrderRepository1 {

    suspend fun createOrder(order: Order): Result<Order>

    fun getTodayOrders(): Flow<List<Order>>

    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>

    /**
     * @param completedAt timestamp ketika pesanan selesai, hanya digunakan saat status COMPLETED
     */
    suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        completedAt: Long? = null
    ): Result<Boolean>
}
