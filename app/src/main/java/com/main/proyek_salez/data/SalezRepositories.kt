package com.main.proyek_salez.data

import com.main.proyek_salez.ui.menu.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class SalezRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val cartItemDao: CartItemDao,
    private val orderDao: OrderDao
) {
    suspend fun insertFoodItems(foodItems: List<FoodItemEntity>) {
        foodItemDao.insertAll(foodItems)
    }

    suspend fun deleteAllFoodItems() {
        foodItemDao.deleteAll()
    }

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItem>> {
        return foodItemDao.getFoodItemsByCategory(category)
            .map { entities -> entities.map { it.toFoodItem() } }
    }

    fun searchFoodItems(name: String): Flow<List<FoodItem>> {
        return foodItemDao.searchFoodItems(name)
            .map { entities -> entities.map { it.toFoodItem() } }
    }

    suspend fun getFoodItemById(id: Int): FoodItem? {
        return foodItemDao.getFoodItemById(id)?.toFoodItem()
    }

    fun getCartItems(): Flow<List<CartItemEntity>> {
        return cartItemDao.getAllCartItems()
    }

    suspend fun addToCart(foodItem: FoodItem) {
        val existingCartItem = cartItemDao.getCartItemByFoodItemId(foodItem.id.toInt())
        if (existingCartItem != null) {
            cartItemDao.update(existingCartItem.copy(quantity = existingCartItem.quantity + 1))
        } else {
            cartItemDao.insert(CartItemEntity(foodItemId = foodItem.id.toInt(), quantity = 1))
        }
    }

    suspend fun decrementCartItem(foodItem: FoodItem) {
        val existingCartItem = cartItemDao.getCartItemByFoodItemId(foodItem.id.toInt())
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