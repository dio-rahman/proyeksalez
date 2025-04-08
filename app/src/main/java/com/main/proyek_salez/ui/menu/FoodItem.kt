package com.main.proyek_salez.ui.menu

data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val rating: String,
    val reviews: String,
    val imageRes: Int,
    val isPopular: Boolean = false
)