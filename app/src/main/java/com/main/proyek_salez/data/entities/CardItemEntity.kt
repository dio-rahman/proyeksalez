package com.main.proyek_salez.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = FoodItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["foodItemId"])]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val cartItemId: Int = 0,
    val foodItemId: Int,
    val quantity: Int
)