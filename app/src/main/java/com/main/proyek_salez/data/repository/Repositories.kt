package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.models.MenuItem
import com.main.proyek_salez.data.models.Order
import com.main.proyek_salez.data.models.OrderStatus
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
