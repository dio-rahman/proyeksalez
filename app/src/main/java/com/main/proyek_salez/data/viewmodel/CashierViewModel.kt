package com.main.proyek_salez.data.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.CategoryEntity
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val repository: CashierRepository
) : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage = _errorMessage

    val cartItems: Flow<List<CartItemWithFood>> = repository.getAllCartItems()
        .catch { e ->
            Log.e("CashierViewModel", "Error in cartItems flow: ${e.message}")
            _errorMessage.value = "Gagal memuat keranjang: ${e.message}"
            emit(emptyList())
        }

    val totalPrice: Flow<String> = cartItems
        .map { items ->
            try {
                val total = items.sumOf { item ->
                    (item.foodItem.price * item.cartItem.quantity).toLong()
                }
                "Rp ${String.format("%,d", total)}"
            } catch (e: Exception) {
                Log.e("CashierViewModel", "Error calculating total price: ${e.message}")
                "Rp 0"
            }
        }
        .catch { e ->
            Log.e("CashierViewModel", "Error in totalPrice flow: ${e.message}")
            emit("Rp 0")
        }

    val customerName = mutableStateOf("")
    val checkoutRequested = mutableStateOf(false)

    fun getFoodItemsByCategory(category: String): Flow<List<FoodItemEntity>> {
        return repository.getFoodItemsByCategory(category)
            .catch { e ->
                Log.e("CashierViewModel", "Error getting food items by category: ${e.message}")
                _errorMessage.value = "Gagal memuat menu kategori $category: ${e.message}"
                emit(emptyList())
            }
    }

    fun addToCart(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.addToCart(foodItem)
                Log.d("CashierViewModel", "Added to cart: ${foodItem.name} (ID: ${foodItem.id})")
            } catch (e: Exception) {
                Log.e("CashierViewModel", "Failed to add to cart: ${e.message}")
                _errorMessage.value = "Gagal menambah ${foodItem.name} ke keranjang: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllFoodItems(): Flow<List<FoodItemEntity>> {
        return repository.getAllFoodItems()
            .catch { e ->
                Log.e("CashierViewModel", "Error getting all food items: ${e.message}")
                _errorMessage.value = "Gagal memuat semua menu: ${e.message}"
                emit(emptyList())
            }
    }

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return repository.getAllOrders()
            .catch { e ->
                Log.e("CashierViewModel", "Error getting all orders: ${e.message}")
                _errorMessage.value = "Gagal memuat riwayat pesanan: ${e.message}"
                emit(emptyList())
            }
    }

    suspend fun getFoodItemById(id: Long): FoodItemEntity? {
        return try {
            repository.getFoodItemById(id)
        } catch (e: Exception) {
            Log.e("CashierViewModel", "Error getting food item by ID: ${e.message}")
            _errorMessage.value = "Gagal memuat detail menu: ${e.message}"
            null
        }
    }

    fun decrementItem(foodItem: FoodItemEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.decrementItem(foodItem)
                Log.d("CashierViewModel", "Decremented item: ${foodItem.name} (ID: ${foodItem.id})")
            } catch (e: Exception) {
                Log.e("CashierViewModel", "Failed to decrement item: ${e.message}")
                _errorMessage.value = "Gagal mengurangi ${foodItem.name}: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.clearCart()
                customerName.value = ""
                checkoutRequested.value = false
                Log.d("CashierViewModel", "Cart cleared")
            } catch (e: Exception) {
                Log.e("CashierViewModel", "Failed to clear cart: ${e.message}")
                _errorMessage.value = "Gagal mengosongkan keranjang: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllCartItems(): Flow<List<CartItemWithFood>> {
        return cartItems
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return repository.getAllCategories()
            .catch { e ->
                Log.e("CashierViewModel", "Error getting categories: ${e.message}")
                _errorMessage.value = "Gagal memuat kategori: ${e.message}"
                emit(emptyList())
            }
    }

    fun createOrder(paymentMethod: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val currentCartItems = cartItems.first()
                if (currentCartItems.isNotEmpty() && customerName.value.isNotBlank()) {
                    repository.createOrder(customerName.value, currentCartItems, paymentMethod)
                    Log.d("CashierViewModel", "Order created for ${customerName.value} with ${currentCartItems.size} items")

                    customerName.value = ""
                    checkoutRequested.value = false
                } else {
                    val errorMsg = when {
                        currentCartItems.isEmpty() -> "Keranjang kosong"
                        customerName.value.isBlank() -> "Nama pelanggan harus diisi"
                        else -> "Tidak dapat membuat pesanan"
                    }
                    Log.e("CashierViewModel", "Cannot create order: $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e("CashierViewModel", "Failed to create order: ${e.message}")
                _errorMessage.value = "Gagal membuat pesanan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getRecommendedItems(): Flow<List<FoodItemEntity>> {
        return repository.getRecommendedItems()
            .catch { e: Throwable ->
                Log.e("CashierViewModel", "Error getting recommended items: ${e.message}")
                _errorMessage.value = "Gagal memuat rekomendasi: ${e.message}"
                emit(emptyList())
            }
    }
}