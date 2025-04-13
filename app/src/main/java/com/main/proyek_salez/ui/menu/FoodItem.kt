package com.main.proyek_salez.ui.menu

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: String,
    val rating: String,
    val reviews: String,
    val imageRes: Int,
    val isPopular: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoodItem) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}