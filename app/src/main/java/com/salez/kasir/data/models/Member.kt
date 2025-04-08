package com.salez.kasir.data.models

data class Member(
    val memberId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String? = null,
    val joinDate: Long = System.currentTimeMillis(),
    val totalSpent: Double = 0.0,
    val totalOrders: Int = 0,
    val discountPercentage: Double = 5.0 // Default 5%
)