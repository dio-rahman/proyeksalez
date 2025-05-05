package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "food_items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class FoodItemEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imagePath: String?,
    val categoryId: Long
)
