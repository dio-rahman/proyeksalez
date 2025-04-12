package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.entities.CartItemEntity
import com.main.proyek_salez.data.SalezRepository
import com.main.proyek_salez.ui.menu.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: SalezRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems.asStateFlow()
    val customerName = mutableStateOf("")

    init {
        viewModelScope.launch {
            repository.getCartItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun addToCart(item: FoodItem) {
        viewModelScope.launch {
            repository.addToCart(item)
        }
    }

    fun decrementItem(item: FoodItem) {
        viewModelScope.launch {
            repository.decrementCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun getTotalPrice(): String {
        val totalPrice = cartItems.value.sumOf { cartItem ->
            viewModelScope.launch {
                val foodItem = repository.getFoodItemById(cartItem.foodItemId)
                foodItem?.let {
                    val priceString = it.price.replace("Rp ", "").replace(".", "")
                    try {
                        priceString.toInt() * cartItem.quantity
                    } catch (e: NumberFormatException) {
                        0
                    }
                } ?: 0
            }.join()
        }
        return "Rp ${totalPrice.toString().chunked(3).joinToString(".")}"
    }

    fun createOrder() {
        viewModelScope.launch {
            repository.createOrder(
                customerName = customerName.value,
                totalPrice = getTotalPrice(),
                cartItems = cartItems.value
            )
        }
    }
}