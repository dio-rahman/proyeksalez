package com.salez.kasir.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_pesanan")
data class DataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama_menu: String,
    val harga_menu: Int,
    val biaya_menu: Int,
    val jenis_pembayaran_menu: String
)