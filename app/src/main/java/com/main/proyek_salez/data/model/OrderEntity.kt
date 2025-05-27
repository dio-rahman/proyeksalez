package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    @PropertyName("customerName") val customerName: String = "",
    @PropertyName("totalPrice") val totalPrice: Long = 0L,
    @PropertyName("orderDate") val orderDate: Timestamp = Timestamp.now(),
    @PropertyName("items") val items: List<Map<String, Any>> = emptyList(), // Changed from List<CartItemEntity>
    @PropertyName("paymentMethod") val paymentMethod: String = "",
    @PropertyName("status") val status: String = "open"
) {
    constructor() : this(
        orderId = 0,
        customerName = "",
        totalPrice = 0L,
        orderDate = Timestamp.now(),
        items = emptyList(),
        paymentMethod = "",
        status = "open"
    )

    // Helper methods to convert between LocalDateTime and Timestamp
    fun getOrderDateAsLocalDateTime(): LocalDateTime {
        return orderDate.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    companion object {
        fun fromLocalDateTime(dateTime: LocalDateTime): Timestamp {
            return Timestamp(
                java.util.Date.from(
                    dateTime.atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }
    }
}