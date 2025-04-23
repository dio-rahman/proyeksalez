package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val customerName: String,
    val totalPrice: String,
    val orderDate: LocalDateTime,
    val items: List<CartItemEntity>
)