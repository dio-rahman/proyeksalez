package com.salez.kasir.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility class untuk format harga dan tanggal
 */
object FormatUtils {
    /**
     * Format harga sebagai mata uang Rupiah
     */
    fun formatPrice(price: Double): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return currencyFormat.format(price)
    }

    /**
     * Format harga untuk ditampilkan singkat (untuk tombol, dll)
     */
    fun formatPriceShort(price: Double): String {
        return when {
            price >= 1000000 -> String.format("%.1fJt", price / 1000000)
            price >= 1000 -> String.format("%.0fK", price / 1000)
            else -> String.format("%.0f", price)
        }
    }

    /**
     * Format tanggal dan waktu
     */
    fun formatDateTime(timestamp: Long, pattern: String = "dd MMM yyyy, HH:mm"): String {
        val dateFormat = java.text.SimpleDateFormat(pattern, Locale("id"))
        return dateFormat.format(java.util.Date(timestamp))
    }

    /**
     * Format waktu saja
     */
    fun formatTime(timestamp: Long): String {
        return formatDateTime(timestamp, "HH:mm")
    }
}