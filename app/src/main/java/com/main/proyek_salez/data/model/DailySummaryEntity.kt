package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val totalRevenue: Double,
    val totalMenuItems: Int,
    val totalCustomers: Int,
    val closedAt: LocalDateTime,
    val previousRevenue: Double? = null,
    val previousMenuItems: Int? = null,
    val previousCustomers: Int? = null
)