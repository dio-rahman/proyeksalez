package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashierRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val foodDao: FoodDao,
    private val orderDao: OrderDao
) {
    suspend fun insertFoodItems(foodItems: List<FoodItemEntity>) {
        foodDao.insertAll(foodItems)
    }

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> {
        return foodDao.getFoodItemsByCategory(category)
    }

    fun searchFoodItems(name: String): Flow<List<FoodItemEntity>> {
        return foodDao.searchFoodItems(name)
    }

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return foodDao.getFoodItemById(id)
    }

    fun getCartItems(): Flow<List<CartItemEntity>> {
        return cartItemDao.getAllCartItems()
    }

    suspend fun addToCart(foodItem: FoodItemEntity) {
        val existingCartItem = cartItemDao.getCartItemByFoodItemId(foodItem.id)
        if (existingCartItem != null) {
            cartItemDao.update(existingCartItem.copy(quantity = existingCartItem.quantity + 1))
        } else {
            cartItemDao.insert(CartItemEntity(foodItemId = foodItem.id, quantity = 1))
        }
    }

    suspend fun decrementCartItem(foodItem: FoodItemEntity) {
        val existingCartItem = cartItemDao.getCartItemByFoodItemId(foodItem.id)
        if (existingCartItem != null) {
            if (existingCartItem.quantity <= 1) {
                cartItemDao.delete(existingCartItem)
            } else {
                cartItemDao.update(existingCartItem.copy(quantity = existingCartItem.quantity - 1))
            }
        }
    }

    suspend fun clearCart() {
        cartItemDao.clearCart()
    }

    suspend fun createOrder(customerName: String, totalPrice: String, cartItems: List<CartItemEntity>) {
        orderDao.insert(
            OrderEntity(
                customerName = customerName,
                totalPrice = totalPrice,
                orderDate = LocalDateTime.now(),
                items = cartItems
            )
        )
        clearCart()
    }

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }
}