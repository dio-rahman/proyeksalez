package com.salez.kasir.ui.cashier

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.data.models.Order
import com.salez.kasir.data.models.OrderItem
import com.salez.kasir.data.models.OrderStatus
import com.salez.kasir.data.repository.MenuRepository
import com.salez.kasir.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel untuk mengelola state dalam alur pemesanan.
 * Mengikuti pola MVVM untuk memisahkan UI dari logika bisnis.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val TAG = "OrderViewModel"

    // State untuk kategori menu
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // State untuk item menu
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    // State untuk order saat ini
    private val _currentOrder = MutableStateFlow<Order>(Order())
    val currentOrder: StateFlow<Order> = _currentOrder.asStateFlow()

    // State untuk item dalam keranjang
    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems.asStateFlow()

    // State untuk nomor meja
    private val _tableNumber = MutableStateFlow(1)
    val tableNumber: StateFlow<Int> = _tableNumber.asStateFlow()

    // State untuk status pembuatan order (sukses/gagal)
    private val _orderSuccess = MutableStateFlow<Boolean?>(null)
    val orderSuccess: StateFlow<Boolean?> = _orderSuccess.asStateFlow()

    // State untuk daftar order hari ini (dashboard)
    private val _todayOrders = MutableStateFlow<List<Order>>(emptyList())
    val todayOrders: StateFlow<List<Order>> = _todayOrders.asStateFlow()

    // State untuk daftar order tertunda
    private val _pendingOrders = MutableStateFlow<List<Order>>(emptyList())
    val pendingOrders: StateFlow<List<Order>> = _pendingOrders.asStateFlow()

    // State untuk daftar order selesai
    private val _completedOrders = MutableStateFlow<List<Order>>(emptyList())
    val completedOrders: StateFlow<List<Order>> = _completedOrders.asStateFlow()

    // State untuk menunjukkan loading status
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State untuk error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
    }

    fun loadMenuCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading menu categories...")
                // PERBAIKAN: Menggunakan collect standard, bukan collectLatest
                menuRepository.getAllCategories().collect { categoryList ->
                    Log.d(TAG, "Categories received: ${categoryList.size}")
                    _categories.value = categoryList
                    Log.d(TAG, "Categories loaded: ${categoryList.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories", e)
                _errorMessage.value = "Gagal memuat kategori: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllMenuItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading all menu items...")
                // PERBAIKAN: Menggunakan collect standard, bukan collectLatest
                menuRepository.getAllMenuItems().collect { items ->
                    Log.d(TAG, "Menu items received: ${items.size}")
                    _menuItems.value = items
                    Log.d(TAG, "Menu items loaded: ${items.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading menu items", e)
                _errorMessage.value = "Gagal memuat menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMenuItemsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading menu items by category: $category")
                // PERBAIKAN: Menggunakan collect standard, bukan collectLatest
                menuRepository.getMenuItemsByCategory(category).collect { items ->
                    Log.d(TAG, "Menu items received for category $category: ${items.size}")
                    _menuItems.value = items
                    Log.d(TAG, "Menu items loaded for category $category: ${items.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading menu items by category", e)
                _errorMessage.value = "Gagal memuat menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addItemToOrder(menuItem: MenuItem, quantity: Int, notes: String = "") {
        val existingItemIndex = _orderItems.value.indexOfFirst { it.menuItem.itemId == menuItem.itemId }

        val updatedItems = if (existingItemIndex != -1) {
            // Item sudah ada, update jumlah
            val existingItem = _orderItems.value[existingItemIndex]
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + quantity,
                subtotal = menuItem.price * (existingItem.quantity + quantity),
                notes = if (notes.isNotEmpty()) notes else existingItem.notes
            )
            val items = _orderItems.value.toMutableList()
            items[existingItemIndex] = updatedItem
            items
        } else {
            // Tambahkan sebagai item baru
            val newOrderItem = OrderItem(
                menuItem = menuItem,
                quantity = quantity,
                notes = notes,
                subtotal = menuItem.price * quantity
            )
            _orderItems.value + newOrderItem
        }

        _orderItems.value = updatedItems
        updateOrderTotals()
        Log.d(TAG, "Item added to order: ${menuItem.name}, quantity: $quantity, total items: ${_orderItems.value.size}")
    }

    fun removeItemFromOrder(orderItem: OrderItem) {
        _orderItems.value = _orderItems.value.filter { it.menuItem.itemId != orderItem.menuItem.itemId }
        updateOrderTotals()
        Log.d(TAG, "Item removed from order: ${orderItem.menuItem.name}, remaining items: ${_orderItems.value.size}")
    }

    fun updateItemQuantity(orderItem: OrderItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItemFromOrder(orderItem)
            return
        }

        val updatedItems = _orderItems.value.map {
            if (it.menuItem.itemId == orderItem.menuItem.itemId) {
                it.copy(
                    quantity = newQuantity,
                    subtotal = it.menuItem.price * newQuantity
                )
            } else {
                it
            }
        }

        _orderItems.value = updatedItems
        updateOrderTotals()
        Log.d(TAG, "Item quantity updated: ${orderItem.menuItem.name}, new quantity: $newQuantity")
    }

    fun updateItemNotes(orderItem: OrderItem, notes: String) {
        val updatedItems = _orderItems.value.map {
            if (it.menuItem.itemId == orderItem.menuItem.itemId) {
                it.copy(notes = notes)
            } else {
                it
            }
        }

        _orderItems.value = updatedItems
        Log.d(TAG, "Item notes updated: ${orderItem.menuItem.name}")
    }

    fun setTableNumber(number: Int) {
        _tableNumber.value = number
        updateOrderTotals()
        Log.d(TAG, "Table number set to: $number")
    }

    private fun updateOrderTotals() {
        val items = _orderItems.value
        val totalPrice = items.sumOf { it.subtotal }

        val taxPercentage = 10.0
        val taxAmount = totalPrice * (taxPercentage / 100)

        val finalPrice = totalPrice + taxAmount - 0.0 // Saat ini tidak ada diskon (0.0)

        // Update pesanan saat ini
        _currentOrder.value = _currentOrder.value.copy(
            tableNumber = _tableNumber.value,
            items = items,
            totalPrice = totalPrice,
            taxPercentage = taxPercentage,
            taxAmount = taxAmount,
            discount = 0.0,
            finalPrice = finalPrice
        )
        Log.d(TAG, "Order totals updated: total price: $totalPrice, tax: $taxAmount, final price: $finalPrice, items: ${items.size}")
    }

    fun finalizeOrder(cashierUserId: String, customerId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val totalPrice = _orderItems.value.sumOf { it.subtotal }
                Log.d(TAG, "Finalizing order for table ${_tableNumber.value}, total: $totalPrice")

                // Buat objek pesanan akhir
                val finalOrder = Order(
                    orderId = UUID.randomUUID().toString(),
                    tableNumber = _tableNumber.value,
                    items = _orderItems.value,
                    totalPrice = totalPrice,
                    discount = 0.0,
                    finalPrice = totalPrice,
                    status = OrderStatus.PENDING,
                    customerId = customerId,
                    createdBy = cashierUserId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    completedAt = null // Set null karena pesanan baru dibuat
                )

                // Kirim pesanan ke repository
                val result = orderRepository.createOrder(finalOrder)

                if (result.isSuccess) {
                    _orderSuccess.value = true
                    Log.d(TAG, "Order created successfully: ${finalOrder.orderId}")

                    // Jangan hapus order dulu, tunggu user mengkonfirmasi pembayaran
                    // clearOrder()

                    // Refresh daftar pesanan setelah membuat pesanan baru
                    loadTodayOrders()
                    loadOrdersByStatus(OrderStatus.PENDING)
                } else {
                    _orderSuccess.value = false
                    val errorMsg = "Gagal membuat pesanan: ${result.exceptionOrNull()?.message}"
                    _errorMessage.value = errorMsg
                    Log.e(TAG, errorMsg, result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _orderSuccess.value = false
                val errorMsg = "Error: ${e.message}"
                _errorMessage.value = errorMsg
                Log.e(TAG, "Error finalizing order", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearOrder() {
        _orderItems.value = emptyList()
        _currentOrder.value = Order()
        _tableNumber.value = 1
        Log.d(TAG, "Order cleared")
    }

    fun resetOrderSuccess() {
        _orderSuccess.value = null
        Log.d(TAG, "Order success reset")
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
        Log.d(TAG, "Error message reset")
    }

    fun loadTodayOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading today's orders...")
                orderRepository.getTodayOrders().collect { orders ->
                    _todayOrders.value = orders
                    Log.d(TAG, "Today's orders loaded: ${orders.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading today's orders", e)
                _errorMessage.value = "Gagal memuat pesanan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrdersByStatus(status: OrderStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading orders by status: $status")
                orderRepository.getOrdersByStatus(status).collect { orders ->
                    when (status) {
                        OrderStatus.PENDING -> _pendingOrders.value = orders
                        OrderStatus.COMPLETED -> _completedOrders.value = orders
                        else -> { /* Status lain ditangani sesuai kebutuhan */ }
                    }
                    Log.d(TAG, "Orders loaded for status $status: ${orders.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading orders by status", e)
                _errorMessage.value = "Gagal memuat pesanan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Updating order status: $orderId to $newStatus")
                // Jika status COMPLETED, set completedAt ke waktu saat ini
                val completedAt = if (newStatus == OrderStatus.COMPLETED) {
                    System.currentTimeMillis()
                } else {
                    null
                }

                val result = orderRepository.updateOrderStatus(orderId, newStatus, completedAt)

                if (result.isSuccess) {
                    // Refresh daftar pesanan
                    loadTodayOrders()
                    loadOrdersByStatus(OrderStatus.PENDING)
                    loadOrdersByStatus(OrderStatus.COMPLETED)
                    Log.d(TAG, "Order status updated successfully")
                } else {
                    val errorMsg = "Gagal mengubah status: ${result.exceptionOrNull()?.message}"
                    _errorMessage.value = errorMsg
                    Log.e(TAG, errorMsg, result.exceptionOrNull())
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                _errorMessage.value = errorMsg
                Log.e(TAG, "Error updating order status", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}