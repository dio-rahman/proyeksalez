package com.main.proyek_salez.data.models

enum class UserRole {
    CASHIER, CHEF, MANAGER
}

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CASHIER,
    val createdAt: Long = System.currentTimeMillis()
)
