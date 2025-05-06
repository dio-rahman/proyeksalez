package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.ViewModel
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val repository: CashierRepository
) : ViewModel() {

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> {
        return repository.getFoodItemsByCategory(category)
    }

    suspend fun addToCart(foodItem: FoodItemEntity) {
        repository.addToCart(foodItem)
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> {
        return repository.getAllFoodItems()
    }

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return repository.getAllOrders()
    }

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return repository.getFoodItemById(id)
    }

    suspend fun decrementItem(foodItem: FoodItemEntity) {
        repository.decrementItem(foodItem)
    }

    suspend fun clearCart() {
        repository.clearCart()
    }

    suspend fun getallCartItems(): Flow<List<CartItemWithFood>> {
        return repository.getAllCartItems()

    }
}