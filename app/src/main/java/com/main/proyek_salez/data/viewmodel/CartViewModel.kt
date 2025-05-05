package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.repository.SalezRepository
import com.main.proyek_salez.ui.menu.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class CartItemWithFood(
    val cartItem: CartItemEntity,
    val foodItem: FoodItem
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: SalezRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemWithFood>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithFood>> = _cartItems.asStateFlow()
    val customerName = mutableStateOf("")

    init {
        viewModelScope.launch {
            repository.getCartItems().collect { items ->
                val cartItemsWithFood = items.mapNotNull { cartItem ->
                    val foodItem = repository.getFoodItemById(cartItem.foodItemId.toLong())
                    foodItem?.let { CartItemWithFood(cartItem, it) }
                }
                _cartItems.value = cartItemsWithFood
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

    suspend fun getTotalPrice(): String {
        val totalPrice = _cartItems.value.sumOf { cartItemWithFood ->
            val foodItem = cartItemWithFood.foodItem
            val priceString = foodItem.price.replace("Rp ", "").replace(".", "")
            try {
                priceString.toBigDecimal() * cartItemWithFood.cartItem.quantity.toBigDecimal()
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
        }
        val formattedPrice = totalPrice.toLong().toString().reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
        return "Rp $formattedPrice"
    }

    fun createOrder() {
        viewModelScope.launch {
            repository.createOrder(
                customerName = customerName.value,
                totalPrice = getTotalPrice(),
                cartItems = _cartItems.value.map { it.cartItem }
            )
        }
    }

    fun searchFoodItems(query: String): Flow<List<FoodItem>> {
        return repository.searchFoodItems(query)
    }
}