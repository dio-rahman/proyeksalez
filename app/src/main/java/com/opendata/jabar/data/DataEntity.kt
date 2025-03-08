package com.opendata.jabar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rata_rata_lama_sekolah")
data class DataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kode_provinsi: Int,
    val nama_provinsi: String,
    val kode_kabupaten_kota: Int,
    val nama_kabupaten_kota: String,
    val rata_rata_lama_sekolah: Double,
    val satuan: String,
    val tahun: Int
)