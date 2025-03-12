package com.salez.kasir.data

import androidx.lifecycle.LiveData

class DataRepository(private val dataDao: DataDao) {

    val allData: LiveData<List<DataEntity>> = dataDao.getAllData()

    suspend fun insert(data: DataEntity) {
        dataDao.insert(data)
    }

    fun getDataById(id: Int): LiveData<DataEntity?> {
        return dataDao.getDataById(id)
    }

    suspend fun update(data: DataEntity) {
        dataDao.update(data)
    }

    suspend fun delete(data: DataEntity) {
        dataDao.delete(data)
    }

    suspend fun deleteAllData() {
        dataDao.deleteAllData()
    }
}