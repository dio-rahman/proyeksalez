package com.salez.kasir.data.repository.impl

import android.util.Log
import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.data.repository.MenuRepository
import com.salez.kasir.data.repository.MenuRepository1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi dari MenuRepository
 *
 * Dalam versi produksi, ini akan terhubung ke database atau API.
 * Untuk saat ini, ini hanya menyediakan data dummy sebagai contoh.
 */
@Singleton
class MenuRepositoryImpl @Inject constructor() : MenuRepository1 {

    private val TAG = "MenuRepositoryImpl"

    // Kategori menu dummy - HARDCODED untuk memastikan data selalu ada
    private val categories = listOf(
        "Makanan", "Minuman", "Camilan", "Dessert"
    )

    // Data menu dummy - HARDCODED untuk memastikan data selalu ada
    private val menuItems = listOf(
        MenuItem(
            itemId = "item1",
            name = "Nasi Goreng",
            description = "Nasi goreng dengan telur, ayam, dan sayuran",
            price = 25000.0,
            category = "Makanan",
            imageUrl = "https://example.com/nasigoreng.jpg",
            isAvailable = true,
            preparationTime = 15 // 15 menit
        ),
        MenuItem(
            itemId = "item2",
            name = "Mie Goreng",
            description = "Mie goreng dengan telur, ayam, dan sayuran",
            price = 22000.0,
            category = "Makanan",
            imageUrl = "https://example.com/miegoreng.jpg",
            isAvailable = true,
            preparationTime = 12 // 12 menit
        ),
        MenuItem(
            itemId = "item3",
            name = "Es Teh",
            description = "Teh manis dingin",
            price = 7000.0,
            category = "Minuman",
            imageUrl = "https://example.com/esteh.jpg",
            isAvailable = true,
            preparationTime = 3 // 3 menit
        ),
        MenuItem(
            itemId = "item4",
            name = "Kopi Hitam",
            description = "Kopi hitam panas",
            price = 10000.0,
            category = "Minuman",
            imageUrl = "https://example.com/kopihitam.jpg",
            isAvailable = true,
            preparationTime = 5 // 5 menit
        ),
        MenuItem(
            itemId = "item5",
            name = "Pisang Goreng",
            description = "Pisang goreng dengan tepung crispy",
            price = 15000.0,
            category = "Camilan",
            imageUrl = "https://example.com/pisanggoreng.jpg",
            isAvailable = true,
            preparationTime = 10 // 10 menit
        ),
        MenuItem(
            itemId = "item6",
            name = "Es Krim",
            description = "Es krim vanilla dengan topping coklat",
            price = 18000.0,
            category = "Dessert",
            imageUrl = "https://example.com/eskrim.jpg",
            isAvailable = true,
            preparationTime = 2 // 2 menit
        ),
    )

    override fun getAllCategories(): Flow<List<String>> = flow {
        // PERBAIKAN: Tidak ada delay, langsung emit data
        Log.d(TAG, "Emitting categories: ${categories.size}")
        emit(categories)
    }

    override fun getAllMenuItems(): Flow<List<MenuItem>> = flow {
        // PERBAIKAN: Tidak ada delay, langsung emit data
        Log.d(TAG, "Emitting all menu items: ${menuItems.size}")
        emit(menuItems)
    }

    override fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>> = flow {
        // PERBAIKAN: Tidak ada delay, langsung emit data
        val filteredItems = menuItems.filter { it.category == category }
        Log.d(TAG, "Emitting menu items for category $category: ${filteredItems.size}")
        emit(filteredItems)
    }
}