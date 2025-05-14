package com.main.proyek_salez.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.main.proyek_salez.data.model.DailySummaryEntity

@Dao
interface DailySummaryDao {
    @Insert
    suspend fun insert(summary: DailySummaryEntity)

    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    suspend fun getSummaryByDate(date: String): DailySummaryEntity?

    @Query("SELECT * FROM daily_summaries ORDER BY closedAt DESC LIMIT 1")
    suspend fun getLatestSummary(): DailySummaryEntity?
}