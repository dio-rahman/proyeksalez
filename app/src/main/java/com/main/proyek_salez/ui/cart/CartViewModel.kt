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
            val updatedItems = currentItems + (item to (currentQuantity + 1))
            println("Cart updated: $updatedItems")
            updatedItems
        }
    }

    fun decrementItem(item: FoodItem) {
        _cartItems.update { currentItems ->
            val currentQuantity = currentItems[item] ?: 0
            if (currentQuantity <= 1) {
                val updatedItems = currentItems - item
                println("Item removed: $updatedItems")
                updatedItems
            } else {
                val updatedItems = currentItems + (item to (currentQuantity - 1))
                println("Cart updated: $updatedItems")
                updatedItems
            }
        }
    }

    fun removeItem(item: FoodItem) {
        _cartItems.update { currentItems ->
            val updatedItems = currentItems - item
            println("Item removed: $updatedItems")
            updatedItems
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        println("Cart cleared")
    }

    fun getItemQuantity(item: FoodItem): Int {
        return cartItems.value[item] ?: 0
    }

    fun getTotalPrice(): String {
        val totalPrice = cartItems.value.entries.sumOf { (item, quantity) ->
            val priceString = item.price.replace("Rp ", "").replace(".", "")
            try {
                priceString.toInt() * quantity
            } catch (e: NumberFormatException) {
                0
            }
        }
        return "Rp ${totalPrice.toString().chunked(3).joinToString(".")}"
    }
}