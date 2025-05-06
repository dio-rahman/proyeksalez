package com.main.proyek_salez.data.dao

import androidx.room.*
import com.main.proyek_salez.data.model.CartItemEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Insert
    suspend fun insert(cartItem: CartItemEntity)

    @Update
    suspend fun update(cartItem: CartItemEntity)

    @Delete
    suspend fun delete(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT * FROM cart_items WHERE foodItemId = :foodItemId")
    suspend fun getCartItemByFoodId(foodItemId: Long): CartItemEntity?

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getCartItemsWithFood(): Flow<List<CartItemWithFood>>
}