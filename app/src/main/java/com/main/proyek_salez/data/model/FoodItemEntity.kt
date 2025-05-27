package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

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
    @PropertyName("name") val name: String,
    @PropertyName("description") val description: String,
    @PropertyName("price") val price: Double,
    @PropertyName("imagePath") val imagePath: String? = "",
    @PropertyName("categoryId") val categoryId: Long,
    @PropertyName("searchKeywords") val searchKeywords: List<String> = emptyList(),
){
    // Konstruktor tanpa argumen untuk Firestore
    constructor() : this(0L, "", "", 0.0, null, 0L, emptyList())
}