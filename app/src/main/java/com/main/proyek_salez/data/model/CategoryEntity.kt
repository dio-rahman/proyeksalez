package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Long = 0L,
    @PropertyName("name") val name: String = ""
) {
    constructor() : this(0L, "")
}