package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

data class CartItemWithFood(
    val cartItem: CartItemEntity,
    val foodItem: FoodItemEntity
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CashierRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemWithFood>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithFood>> = _cartItems.asStateFlow()
    val customerName = mutableStateOf("")

    init {
        viewModelScope.launch {
            repository.getCartItems().collect { items ->
                val cartItemsWithFood = items.mapNotNull { cartItem ->
                    val foodItem = repository.getFoodItemById(cartItem.foodItemId)
                    foodItem?.let { CartItemWithFood(cartItem, it) }
                }
                _cartItems.value = cartItemsWithFood
            }
        }
    }

    fun addToCart(item: FoodItemEntity) {
        viewModelScope.launch {
            repository.addToCart(item)
        }
    }

    fun decrementItem(item: FoodItemEntity) {
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
        val totalPrice = _cartItems.value.sumOf { cartItemWithFood ->
            cartItemWithFood.foodItem.price * cartItemWithFood.cartItem.quantity
        }
        val formatter = DecimalFormat("#,###")
        val formattedPrice = formatter.format(totalPrice).replace(",", ".")
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

    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>> {
        return repository.searchFoodItems(query)
    }
}