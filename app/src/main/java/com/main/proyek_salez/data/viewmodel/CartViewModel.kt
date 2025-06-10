package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cashierRepository: CashierRepository
) : ViewModel() {

    val cartItems: Flow<List<CartItemWithFood>> = cashierRepository.getAllCartItems()

    fun addToCart(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            cashierRepository.addToCart(foodItem)
        }
    }

    fun decrementItem(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            cashierRepository.decrementItem(foodItem)
        }
    }

    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>> =
        cashierRepository.getAllFoodItems().map { items ->
            items.filter { it.searchKeywords.contains(query.lowercase()) }
        }
}