package com.salez.kasir.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.salez.kasir.data.AppDatabase
import com.salez.kasir.data.DataEntity
import com.salez.kasir.data.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DataRepository
    val dataList: LiveData<List<DataEntity>>
    private val _importedData = MutableLiveData<String>()
    val importedData: LiveData<String> = _importedData

    init {
        val dataDao = AppDatabase.getDatabase(application).dataDao()
        repository = DataRepository(dataDao)
        dataList = repository.allData
    }

    fun insertData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(data)
        }
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(data)
        }
    }

    fun deleteData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(data)
        }
    }

    fun getDataById(id: Int): LiveData<DataEntity?> {
        return repository.getDataById(id)
    }

    fun updateImportedData(data: String) {
        _importedData.value = data
    }

    fun importDataFromExcel(dataList: List<DataEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            dataList.forEach { data ->
                repository.insert(data)
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }
}