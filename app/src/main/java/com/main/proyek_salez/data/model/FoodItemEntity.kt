package com.main.proyek_salez.data.model

import com.google.firebase.firestore.PropertyName

data class FoodItemEntity(
    @PropertyName("id") val id: Long,
    @PropertyName("name") val name: String,
    @PropertyName("description") val description: String,
    @PropertyName("price") val price: Double,
    @PropertyName("imagePath") val imagePath: String? = "",
    @PropertyName("categoryId") val categoryId: String,
    @PropertyName("searchKeywords") val searchKeywords: List<String> = emptyList(),
) {
    constructor() : this(0L, "", "", 0.0, null, "", emptyList())
}