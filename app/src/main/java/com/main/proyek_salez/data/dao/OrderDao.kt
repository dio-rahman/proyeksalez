package com.main.proyek_salez.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.main.proyek_salez.data.model.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?

    @Query("SELECT * FROM orders")
    fun getOrderHistory(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE date(orderDate) = :date")
    fun getDailyOrders(date: String): Flow<List<OrderEntity>>

    @Update
    suspend fun updateOrders(vararg orders: OrderEntity)
}