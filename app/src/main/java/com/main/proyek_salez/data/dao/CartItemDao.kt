package com.main.proyek_salez.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.main.proyek_salez.data.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Insert
    suspend fun insert(cartItem: CartItemEntity)

    @Update
    suspend fun update(cartItem: CartItemEntity)

    @Delete
    suspend fun delete(cartItem: CartItemEntity)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE foodItemId = :foodItemId")
    suspend fun getCartItemByFoodItemId(foodItemId: Long): CartItemEntity?

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}