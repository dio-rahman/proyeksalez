package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manager_profile")
data class ManagerProfileEntity(
    @PrimaryKey val id: Int = 1, // ID tetap 1 untuk profil tunggal
    val username: String,
    val email: String,
    val nickname: String,
    val profilePhotoUri: String?
)