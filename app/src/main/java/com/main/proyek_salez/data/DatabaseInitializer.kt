package com.main.proyek_salez.data

import com.main.proyek_salez.data.entities.FoodItemEntity
import com.salez.proyek_salez.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val repository: SalezRepository
) {
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val foodItems = listOf(
            FoodItemEntity(0, "Mi Goreng El Salvadore", "Olahan dengan nuansa tradisional yang berani tercipta dalam dan nuansa elegan", "Rp 12.000", "4.5", "305 Penilaian", R.drawable.salez_logo, true, "food"),
            FoodItemEntity(1, "Nasi Goreng Spesial", "Nasi goreng dengan campuran bumbu khas dan topping telur", "Rp 15.000", "4.7", "420 Penilaian", R.drawable.salez_logo, false, "food"),
            FoodItemEntity(2, "Ayam Bakar Madu", "Ayam bakar dengan balutan madu manis dan rempah pilihan", "Rp 20.000", "4.8", "250 Penilaian", R.drawable.salez_logo, true, "food"),
            FoodItemEntity(3, "Soto Ayam Lamongan", "Soto ayam khas Lamongan dengan kuah gurih dan suwiran ayam", "Rp 18.000", "4.6", "180 Penilaian", R.drawable.salez_logo, false, "food"),
            FoodItemEntity(4, "Bakso Beranak", "Bakso besar berisi bakso kecil dengan kuah kaldu sapi", "Rp 25.000", "4.9", "350 Penilaian", R.drawable.salez_logo, true, "food"),

            FoodItemEntity(0, "Jus Alpukat", "Jus alpukat segar dengan campuran susu dan gula alami", "Rp 10.000", "4.5", "200 Penilaian", R.drawable.salez_logo, true, "drink"),
            FoodItemEntity(1, "Susu Cokelat", "Susu cokelat hangat dengan rasa manis yang pas", "Rp 8.000", "4.3", "150 Penilaian", R.drawable.salez_logo, false, "drink"),
            FoodItemEntity(2, "Kopi Hitam", "Kopi hitam murni tanpa gula, pahit dan aromatik", "Rp 7.000", "4.6", "180 Penilaian", R.drawable.salez_logo, true, "drink"),
            FoodItemEntity(3, "Teh Tarik", "Teh tarik khas dengan busa lembut dan rasa manis", "Rp 9.000", "4.4", "160 Penilaian", R.drawable.salez_logo, false, "drink"),
            FoodItemEntity(4, "Es Kelapa Muda", "Kelapa muda segar dengan tambahan es batu", "Rp 12.000", "4.8", "220 Penilaian", R.drawable.salez_logo, true, "drink"),

            FoodItemEntity(0, "Es Krim Lava", "Es krim cokelat dengan saus lava panas di dalamnya", "Rp 15.000", "4.7", "250 Penilaian", R.drawable.salez_logo, true, "other"),
            FoodItemEntity(1, "Roti Bakar Matcha", "Roti bakar dengan selai matcha dan taburan keju", "Rp 12.000", "4.5", "180 Penilaian", R.drawable.salez_logo, false, "other"),
            FoodItemEntity(2, "Puding Rainbow", "Puding warna-warni dengan rasa buah segar", "Rp 10.000", "4.6", "200 Penilaian", R.drawable.salez_logo, true, "other"),
            FoodItemEntity(3, "Kue Cubit", "Kue cubit mini dengan topping cokelat dan meses", "Rp 8.000", "4.4", "150 Penilaian", R.drawable.salez_logo, false, "other"),
            FoodItemEntity(4, "Donat Unicorn", "Donat berwarna cerah dengan glaze manis unik", "Rp 14.000", "4.8", "230 Penilaian", R.drawable.salez_logo, true, "other")
        )
        repository.insertFoodItems(foodItems)
    }
}