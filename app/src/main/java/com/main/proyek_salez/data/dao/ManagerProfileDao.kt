package com.main.proyek_salez.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.main.proyek_salez.data.model.ManagerProfileEntity

@Dao
interface ManagerProfileDao {
    @Query("SELECT * FROM manager_profile WHERE id = 1")
    suspend fun getProfile(): ManagerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ManagerProfileEntity)
}