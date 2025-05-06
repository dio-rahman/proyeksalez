package com.main.proyek_salez.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.repository.ManagerRepository
import com.main.proyek_salez.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val repository: ManagerRepository
) : ViewModel() {
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    private val _foodItems = MutableStateFlow<List<FoodItemEntity>>(emptyList())
    val foodItems: StateFlow<List<FoodItemEntity>> = _foodItems

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadCategories()
        loadFoodItems()
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
                imagePath = imagePath,
                categoryId = categoryId
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
                imagePath = imagePath,
                categoryId = categoryId
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
}