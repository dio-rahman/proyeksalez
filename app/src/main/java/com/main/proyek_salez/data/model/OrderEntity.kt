package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    @PropertyName("customerName") val customerName: String = "",
    @PropertyName("totalPrice") val totalPrice: Long = 0L,
    @PropertyName("orderDate") val orderDate: Timestamp = Timestamp.now(),
    @PropertyName("items") val items: List<Map<String, Any>> = emptyList(),
    @PropertyName("paymentMethod") val paymentMethod: String = "",
    @PropertyName("status") val status: String = "open"
)