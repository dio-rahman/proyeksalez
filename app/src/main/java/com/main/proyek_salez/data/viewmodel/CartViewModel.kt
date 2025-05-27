package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cashierRepository: CashierRepository
) : ViewModel() {
    val customerName = mutableStateOf("")
    val cartItems: Flow<List<CartItemWithFood>> = cashierRepository.getAllCartItems()
    val checkoutRequested = mutableStateOf(false)
    val totalPrice: StateFlow<String> = cartItems.map { items ->
        val total = items.sumOf { it.foodItem.price * it.cartItem.quantity.toDouble() }.toLong()
        "Rp $total"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Rp 0")

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

    fun clearCart() {
        viewModelScope.launch {
            cashierRepository.clearCart()
        }
    }

    fun resetCustomerName() {
        customerName.value = ""
    }

    suspend fun getTotalPrice(): Long {
        val items = cartItems.first()
        return items.sumOf { it.foodItem.price * it.cartItem.quantity.toDouble() }.toLong()
    }

    fun createOrder(paymentMethod: String) {
        viewModelScope.launch {
            val items = cartItems.first()
            if (items.isNotEmpty()) {
                if (customerName.value.isBlank()) {
                    println("Error: customerName is blank")
                    return@launch
                }
                println("Creating order with customerName: ${customerName.value}")
                cashierRepository.createOrder(
                    customerName = customerName.value,
                    cartItems = items,
                    paymentMethod = paymentMethod
                )
                println("Order created successfully for customer: ${customerName.value}")
            } else {
                println("Error: cartItems is empty")
            }
        }
    }

    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>> = cashierRepository.getAllFoodItems().map { items ->
        items.filter { it.searchKeywords.contains(query.lowercase()) }
    }
}