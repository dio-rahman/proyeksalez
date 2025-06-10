package com.main.proyek_salez.data.model

import com.google.firebase.firestore.PropertyName

data class CategoryEntity(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = ""
)