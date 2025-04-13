package com.main.proyek_salez.data.models

data class Order(
    val orderId: String = "",
    val tableNumber: Int = 0,
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val taxPercentage: Double = 10.0, // Default tax 10%
    val taxAmount: Double = 0.0,
    val discount: Double = 0.0,
    val finalPrice: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val customerId: String? = null,   // Jika customer adalah member
    val createdBy: String = "",       // userId Kasir
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int = 1,
    val notes: String = "",
    val subtotal: Double = menuItem.price * quantity
)

enum class OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED
}
