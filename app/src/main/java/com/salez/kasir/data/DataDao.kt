package com.salez.kasir.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataDao {
    @Insert
    (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<DataEntity>)

    @Query("SELECT * FROM rata_rata_lama_sekolah WHERE tahun = :tahun")
    suspend fun getDataByYear(tahun: Int): List<DataEntity>

    @Query("SELECT * FROM rata_rata_lama_sekolah")
    fun getAllData(): LiveData<List<DataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataEntity)

    @Query("SELECT * FROM rata_rata_lama_sekolah WHERE id = :id")
    fun getDataById(id: Int): LiveData<DataEntity?>

    @Update
    suspend fun update(data: DataEntity)

    @Delete
    suspend fun delete(data: DataEntity)

    @Query("DELETE FROM rata_rata_lama_sekolah")
    suspend fun deleteAllData()
}