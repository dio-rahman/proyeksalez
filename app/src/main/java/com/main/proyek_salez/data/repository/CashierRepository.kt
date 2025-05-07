package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import java.time.LocalDateTime

class CashierRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val orderDao: OrderDao,
    private val foodDao: FoodDao
) {
    fun getAllCartItems(): Flow<List<CartItemWithFood>> {
        return cartItemDao.getCartItemsWithFood()
    }

    suspend fun addToCart(foodItem: FoodItemEntity) {
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

    suspend fun decrementItem(foodItem: FoodItemEntity) {
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

    suspend fun clearCart() {
        cartItemDao.clearCart()
    }

    suspend fun getTotalPrice(cartItems: List<CartItemWithFood>): Long {
        return cartItems.sumOf { it.foodItem.price.toLong() * it.cartItem.quantity.toLong() }
    }

    suspend fun createOrder(customerName: String, cartItems: List<CartItemWithFood>, paymentMethod: String) {
        if (cartItems.isNotEmpty()) {
            val totalPrice = getTotalPrice(cartItems)
            val order = OrderEntity(
                customerName = customerName,
                totalPrice = totalPrice,
                orderDate = LocalDateTime.now(),
                items = cartItems.map { it.cartItem },
                paymentMethod = paymentMethod
            )
            orderDao.insert(order)
        }
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> {
        return foodDao.getAllFoodItems()
    }

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> {
        return foodDao.getFoodItemsByCategory(category)
    }

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return foodDao.getFoodItemById(id)
    }

    fun getOrderHistory(): Flow<List<OrderEntity>> {
        return orderDao.getOrderHistory()
    }

    suspend fun getAllCategories(): List<CategoryEntity> {
        return foodDao.getAllCategories()
    }
}