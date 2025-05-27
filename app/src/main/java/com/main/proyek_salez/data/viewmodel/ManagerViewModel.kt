package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.DailySummaryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.repository.ManagerRepository
import com.main.proyek_salez.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val repository: ManagerRepository
) : ViewModel() {
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _foodItems = MutableStateFlow<List<FoodItemEntity>>(emptyList())
    val foodItems: StateFlow<List<FoodItemEntity>> = _foodItems.asStateFlow()

    private val _popularFoodItems = MutableStateFlow<List<Pair<FoodItemEntity, Int>>>(emptyList())
    val popularFoodItems: StateFlow<List<Pair<FoodItemEntity, Int>>> = _popularFoodItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _summary = MutableStateFlow<DailySummaryEntity?>(null)
    val summary: StateFlow<DailySummaryEntity?> = _summary.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val percentageRevenue = if (summary.value?.previousRevenue != null && summary.value?.previousRevenue!! > 0) {
        val change = ((summary.value?.totalRevenue!! - summary.value?.previousRevenue!!) / summary.value?.previousRevenue!!) * 100
        "${"%.2f".format(change)}%"
    } else "0.00%"

    init {
        loadCategories()
        loadFoodItems()
        loadLatestSummary()
        loadPopularFoodItems()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            repository.getAllFoodItems().collect { items ->
                _foodItems.value = items
            }
        }
    }

    private fun loadPopularFoodItems() {
        viewModelScope.launch {
            try {
                _popularFoodItems.value = repository.getPopularFoodItems()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal memuat menu populer: ${e.message}"
            }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            when (val result = repository.addCategory(CategoryEntity(name = name))) {
                is Result.Success -> {
                    loadCategories()
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            when (val result = repository.deleteCategory(categoryId)) {
                is Result.Success -> {
                    loadCategories()
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun addFoodItem(
        id: Long,
        name: String,
        description: String,
        price: Double,
        imagePath: String?,
        categoryId: Long
    ) {
        viewModelScope.launch {
            val foodItem = FoodItemEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                imagePath = imagePath ?: "",
                categoryId = categoryId,
                searchKeywords = name.lowercase().split(" ")
            )
            when (val result = repository.addFoodItem(foodItem)) {
                is Result.Success -> {
                    loadFoodItems()
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun updateFoodItem(
        id: Long,
        name: String,
        description: String,
        price: Double,
        imagePath: String?,
        categoryId: Long
    ) {
        viewModelScope.launch {
            val foodItem = FoodItemEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                imagePath = imagePath ?: "",
                categoryId = categoryId,
                searchKeywords = name.lowercase().split(" ")
            )
            when (val result = repository.updateFoodItem(foodItem)) {
                is Result.Success -> {
                    loadFoodItems()
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun deleteFoodItem(id: Long) {
        viewModelScope.launch {
            when (val result = repository.deleteFoodItem(id)) {
                is Result.Success -> {
                    loadFoodItems()
                    _errorMessage.value = null
                }
                is Result.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun loadLatestSummary() {
        viewModelScope.launch {
            try {
                val latestSummary = repository.getLatestSummary()
                _summary.value = latestSummary
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Gagal memuat data: ${e.message}"
            }
        }
    }
}