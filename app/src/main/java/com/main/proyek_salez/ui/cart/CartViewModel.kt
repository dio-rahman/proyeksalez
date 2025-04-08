package com.main.proyek_salez.ui.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.main.proyek_salez.ui.menu.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<Map<FoodItem, Int>>(emptyMap())
    val cartItems: StateFlow<Map<FoodItem, Int>> = _cartItems.asStateFlow()

    val customerName = mutableStateOf("")

    fun addToCart(item: FoodItem) {
        _cartItems.update { currentItems ->
            val currentQuantity = currentItems[item] ?: 0
            currentItems + (item to (currentQuantity + 1))
        }
    }

    fun decrementItem(item: FoodItem) {
        _cartItems.update { currentItems ->
            val currentQuantity = currentItems[item] ?: 0
            if (currentQuantity <= 1) {
                currentItems - item
            } else {
                currentItems + (item to (currentQuantity - 1))
            }
        }
    }

    fun removeItem(item: FoodItem) {
        _cartItems.update { currentItems ->
            currentItems - item
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }
}