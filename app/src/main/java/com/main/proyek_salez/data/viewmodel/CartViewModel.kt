package com.main.proyek_salez.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.repository.CashierRepository
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
    private val foodDao: FoodDao,
    private val cashierRepository: CashierRepository
) : ViewModel() {
    val customerName = mutableStateOf("")
    val cartItems: Flow<List<CartItemWithFood>> = cartItemDao.getCartItemsWithFood()
    val checkoutRequested = mutableStateOf(false)

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

    suspend fun getTotalPrice(): Long {
        val items = cartItems.first()
        return items.sumOf { it.foodItem.price * it.cartItem.quantity.toDouble() }.toLong()
    }

    fun createOrder(paymentMethod: String) {
        viewModelScope.launch {
            val items = cartItems.first()
            if (items.isNotEmpty()) {
                cashierRepository.createOrder(
                    customerName = customerName.value,
                    cartItems = items,
                    paymentMethod = paymentMethod
                )
            }
        }
    }

    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>> {
            return foodDao.searchFoodItems(query)
    }
}
