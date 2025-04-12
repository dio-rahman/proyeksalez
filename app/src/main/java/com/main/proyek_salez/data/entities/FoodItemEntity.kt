package com.main.proyek_salez.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.main.proyek_salez.ui.menu.FoodItem

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val rating: String,
    val reviews: String,
    val imageRes: Int,
    val isPopular: Boolean,
    val category: String
)

fun FoodItemEntity.toFoodItem() = FoodItem(
    id = id,
    name = name,
    description = description,
    price = price,
    rating = rating,
    reviews = reviews,
    imageRes = imageRes,
    isPopular = isPopular
)