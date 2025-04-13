package com.main.proyek_salez.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.main.proyek_salez.data.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?
}