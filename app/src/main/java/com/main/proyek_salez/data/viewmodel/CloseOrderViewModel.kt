package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.OrderEntity
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CloseOrderViewModel @Inject constructor(
    private val repository: CashierRepository
) : ViewModel() {
    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private val _closeStatus = MutableStateFlow<String?>(null)
    val closeStatus: StateFlow<String?> = _closeStatus.asStateFlow()

    fun loadDailyOrders(date: String) {
        viewModelScope.launch {
            repository.getDailyOrders(date).collect { orders ->
                _orders.value = orders
            }
        }
    }

    fun closeOrders(date: String) {
        viewModelScope.launch {
            try {
                repository.closeDailyOrders(date)
                _closeStatus.value = "Pesanan harian berhasil ditutup"
            } catch (e: Exception) {
                _closeStatus.value = "Gagal: ${e.message}"
            }
        }
    }
}