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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _summaries = MutableStateFlow<Pair<DailySummaryEntity?, DailySummaryEntity?>>(null to null)
    val summaries: StateFlow<Pair<DailySummaryEntity?, DailySummaryEntity?>> = _summaries.asStateFlow()

    private val _popularFoodItems = MutableStateFlow<List<Pair<FoodItemEntity, Int>>>(emptyList())
    val popularFoodItems: StateFlow<List<Pair<FoodItemEntity, Int>>> = _popularFoodItems.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllCategories().collect { categories ->
                _categories.value = categories
            }
        }
        viewModelScope.launch {
            repository.getAllFoodItems().collect { foodItems ->
                _foodItems.value = foodItems
            }
        }
        viewModelScope.launch {
            val (latest, previous) = repository.getLatestSummary()
            _summaries.value = latest to previous
        }
        viewModelScope.launch {
            _popularFoodItems.value = repository.getPopularFoodItems()
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            when (val result = repository.addCategory(CategoryEntity(name = name))) {
                is Result.Success -> clearErrorMessage()
                is Result.Error -> setErrorMessage(result.message)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteCategory(categoryId)) {
                is Result.Success -> clearErrorMessage()
                is Result.Error -> setErrorMessage(result.message)
            }
        }
    }

    fun addFoodItem(id: Long, name: String, description: String, price: Double, imagePath: String?, categoryId: String) {
        viewModelScope.launch {
            val foodItem = FoodItemEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                imagePath = imagePath,
                categoryId = categoryId
            )
            when (val result = repository.addFoodItem(foodItem)) {
                is Result.Success -> clearErrorMessage()
                is Result.Error -> setErrorMessage(result.message)
            }
        }
    }

    fun updateFoodItem(id: Long, name: String, description: String, price: Double, imagePath: String?, categoryId: String) {
        viewModelScope.launch {
            val foodItem = FoodItemEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                imagePath = imagePath,
                categoryId = categoryId
            )
            when (val result = repository.updateFoodItem(foodItem)) {
                is Result.Success -> clearErrorMessage()
                is Result.Error -> setErrorMessage(result.message)
            }
        }
    }

    fun deleteFoodItem(id: Long) {
        viewModelScope.launch {
            when (val result = repository.deleteFoodItem(id)) {
                is Result.Success -> clearErrorMessage()
                is Result.Error -> setErrorMessage(result.message)
            }
        }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}