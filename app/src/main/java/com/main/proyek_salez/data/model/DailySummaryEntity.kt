package com.main.proyek_salez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @PropertyName("date") val date: String = "", // Format: "yyyy-MM-dd"
    @PropertyName("totalRevenue") val totalRevenue: Double = 0.0,
    @PropertyName("totalMenuItems") val totalMenuItems: Int = 0,
    @PropertyName("totalCustomers") val totalCustomers: Int = 0,
    @PropertyName("closedAt") val closedAt: Timestamp = Timestamp.now(),
    @PropertyName("previousRevenue") val previousRevenue: Double? = null,
    @PropertyName("previousMenuItems") val previousMenuItems: Int? = null,
    @PropertyName("previousCustomers") val previousCustomers: Int? = null
) {
    companion object {
        fun fromLocalDateTime(dateTime: LocalDateTime): Timestamp {
            return Timestamp(
                java.util.Date.from(
                    dateTime.atZone(ZoneId.systemDefault()).toInstant()
                )
            )
        }

        fun createWithLocalDateTime(
            id: Long = 0,
            date: String,
            totalRevenue: Double,
            totalMenuItems: Int,
            totalCustomers: Int,
            closedAt: LocalDateTime,
            previousRevenue: Double? = null,
            previousMenuItems: Int? = null,
            previousCustomers: Int? = null
        ): DailySummaryEntity {
            return DailySummaryEntity(
                id = id,
                date = date,
                totalRevenue = totalRevenue,
                totalMenuItems = totalMenuItems,
                totalCustomers = totalCustomers,
                closedAt = fromLocalDateTime(closedAt),
                previousRevenue = previousRevenue,
                previousMenuItems = previousMenuItems,
                previousCustomers = previousCustomers
            )
        }
    }
}