package com.salez.kasir.data.models

data class MenuItem(
    val itemId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val preparationTime: Int = 0 // dalam menit
)