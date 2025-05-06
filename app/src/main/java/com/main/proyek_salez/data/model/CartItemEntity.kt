package com.main.proyek_salez.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

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
    val foodItemId: Long,
    val quantity: Int
)

data class CartItemWithFood(
    @Embedded val cartItem: CartItemEntity,
    @Relation(
        parentColumn = "foodItemId",
        entityColumn = "id"
    )
    val foodItem: FoodItemEntity
)