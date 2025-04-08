package com.salez.kasir.data.repository.impl

import com.salez.kasir.data.models.Order
import com.salez.kasir.data.models.OrderStatus
import com.salez.kasir.data.repository.OrderRepository
import com.salez.kasir.data.repository.OrderRepository1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi dari OrderRepository
 *
 * Dalam versi produksi, ini akan terhubung ke database atau API.
 * Untuk saat ini, ini hanya menyimpan data di memory sebagai contoh.
 */
@Singleton
class OrderRepositoryImpl @Inject constructor() : OrderRepository1 {

    // Menyimpan data pesanan dalam memory (untuk contoh saja)
    private val orders = MutableStateFlow<List<Order>>(emptyList())

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            // Simulasi network delay
            kotlinx.coroutines.delay(1000)

            // Tambahkan pesanan ke daftar
            val currentOrders = orders.value.toMutableList()
            currentOrders.add(order)
            orders.value = currentOrders

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTodayOrders(): Flow<List<Order>> {
        // Dapatkan timestamp untuk awal hari ini
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = calendar.timeInMillis

        // Filter hanya pesanan hari ini
        return orders.map { orderList ->
            orderList.filter { it.createdAt >= startOfDay }
        }
    }

    override fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> {
        // Filter berdasarkan status
        return orders.map { orderList ->
            orderList.filter { it.status == status }
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        status: OrderStatus,
        completedAt: Long?
    ): Result<Boolean> {
        return try {
            // Simulasi network delay
            kotlinx.coroutines.delay(500)

            val currentOrders = orders.value.toMutableList()
            val orderIndex = currentOrders.indexOfFirst { it.orderId == orderId }

            if (orderIndex != -1) {
                // Update status pesanan
                val updatedOrder = currentOrders[orderIndex].copy(
                    status = status,
                    updatedAt = System.currentTimeMillis(),
                    completedAt = completedAt ?: currentOrders[orderIndex].completedAt
                )
                currentOrders[orderIndex] = updatedOrder
                orders.value = currentOrders
                Result.success(true)
            } else {
                Result.failure(Exception("Pesanan tidak ditemukan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}