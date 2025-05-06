package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val orderDao: OrderDao,
) : ViewModel() {
    val customerName = mutableStateOf("")
    val cartItems: Flow<List<CartItemWithFood>> = cartItemDao.getCartItemsWithFood()

    fun addToCart(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            val existingItem = cartItemDao.getCartItemByFoodId(foodItem.id)
            if (existingItem != null) {
                cartItemDao.update(
                    existingItem.copy(quantity = existingItem.quantity + 1)
                )
            } else {
                cartItemDao.insert(
                    CartItemEntity(foodItemId = foodItem.id, quantity = 1)
                )
            }
        }
    }

    fun decrementItem(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            val existingItem = cartItemDao.getCartItemByFoodId(foodItem.id)
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    cartItemDao.update(
                        existingItem.copy(quantity = existingItem.quantity - 1)
                    )
                } else {
                    cartItemDao.delete(existingItem)
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartItemDao.clearCart()
            customerName.value = ""
        }
    }

    suspend fun getTotalPrice(): String {
        val items = cartItems.first()
        val total = items.sumOf { it.foodItem.price * it.cartItem.quantity.toDouble() }
        return "Rp ${total.toLong()}"
    }

    fun createOrder(paymentMethod: String) {
        viewModelScope.launch {
            val items = cartItems.first().map { it.cartItem }
            if (items.isNotEmpty()) {
                val totalPrice = getTotalPrice()
                val order = OrderEntity(
                    customerName = customerName.value,
                    totalPrice = totalPrice,
                    orderDate = LocalDateTime.now(),
                    items = items,
                    paymentMethod = paymentMethod
                )
                orderDao.insert(order)
            }
        }
    }
}