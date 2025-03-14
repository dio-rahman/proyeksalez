package com.salez.kasir.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DataDao{
    @Insert
    (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataEntity)

    @Insert
    (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<DataEntity>)

    @Query
    ("SELECT * FROM menu_pesanan WHERE biaya_menu = :biaya_menu")
    suspend fun getDataByYear(biaya_menu: Int): List<DataEntity>

    @Query
    ("SELECT * FROM menu_pesanan")
    fun getAllData(): LiveData<List<DataEntity>>

    @Query
    ("SELECT * FROM menu_pesanan WHERE id = :id")
    fun getDataById(id: Int): LiveData<DataEntity?>

    @Query("DELETE FROM menu_pesanan")
    suspend fun deleteAllData()

    @Delete
    suspend fun delete(data: DataEntity)

    @Update
    suspend fun update(data: DataEntity)
}