package com.main.proyek_salez.data.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
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
    private val repository: CashierRepository,
    private val application: Application,
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
                val totalRevenue = _orders.value.sumOf { it.totalPrice }
                sendLocalNotification(date, totalRevenue)
                _closeStatus.value = "Pesanan harian berhasil ditutup"
            } catch (e: Exception) {
                _closeStatus.value = "Gagal: ${e.message}"
            }
        }
    }

    private fun sendLocalNotification(date: String, totalRevenue: Long) {
        val channelId = "close_order_channel"
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat channel notifikasi (hanya untuk API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Close Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi saat pesanan harian ditutup"
            }
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(application, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ganti dengan ikon aplikasi
            .setContentTitle("Pesanan Harian Ditutup")
            .setContentText("Pesanan untuk $date telah ditutup. Total: Rp ${String.format("%,d", totalRevenue)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }
}