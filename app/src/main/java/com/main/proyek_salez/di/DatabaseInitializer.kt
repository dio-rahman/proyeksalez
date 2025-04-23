package com.main.proyek_salez.di

import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.repository.SalezRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val repository: SalezRepository
) {
    suspend fun initialize() = withContext(Dispatchers.IO) {
        // Clear all existing items to avoid ID conflicts
//        repository.deleteAllFoodItems()

        // Insert food items
        val foodItems = listOf(
            FoodItemEntity(
                id = 1,
                name = "Mi Goreng El Salvadore",
                description = "Olahan dengan nuansa tradisional yang berani tercipta dalam dan nuansa elegan",
                price = "Rp 12.000",
                rating = "4.5",
                reviews = "305 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "food"
            ),
            FoodItemEntity(
                id = 2,
                name = "Nasi Goreng Spesial",
                description = "Nasi goreng dengan campuran bumbu khas dan topping telur",
                price = "Rp 15.000",
                rating = "4.7",
                reviews = "420 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "food"
            ),
            FoodItemEntity(
                id = 3,
                name = "Ayam Bakar Madu",
                description = "Ayam bakar dengan balutan madu manis dan rempah pilihan",
                price = "Rp 20.000",
                rating = "4.8",
                reviews = "250 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "food"
            ),
            FoodItemEntity(
                id = 4,
                name = "Soto Ayam Lamongan",
                description = "Soto ayam khas Lamongan dengan kuah gurih dan suwiran ayam",
                price = "Rp 18.000",
                rating = "4.6",
                reviews = "180 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "food"
            ),
            FoodItemEntity(
                id = 5,
                name = "Bakso Beranak",
                description = "Bakso besar berisi bakso kecil dengan kuah kaldu sapi",
                price = "Rp 25.000",
                rating = "4.9",
                reviews = "350 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "food"
            ),
            FoodItemEntity(
                id = 6,
                name = "Jus Alpukat",
                description = "Jus alpukat segar dengan campuran susu dan gula alami",
                price = "Rp 10.000",
                rating = "4.5",
                reviews = "200 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "drink"
            ),
            FoodItemEntity(
                id = 7,
                name = "Susu Cokelat",
                description = "Susu cokelat hangat dengan rasa manis yang pas",
                price = "Rp 8.000",
                rating = "4.3",
                reviews = "150 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "drink"
            ),
            FoodItemEntity(
                id = 8,
                name = "Kopi Hitam",
                description = "Kopi hitam murni tanpa gula, pahit dan aromatik",
                price = "Rp 7.000",
                rating = "4.6",
                reviews = "180 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "drink"
            ),
            FoodItemEntity(
                id = 9,
                name = "Teh Tarik",
                description = "Teh tarik khas dengan busa lembut dan rasa manis",
                price = "Rp 9.000",
                rating = "4.4",
                reviews = "160 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "drink"
            ),
            FoodItemEntity(
                id = 10,
                name = "Es Kelapa Muda",
                description = "Kelapa muda segar dengan tambahan es batu",
                price = "Rp 12.000",
                rating = "4.8",
                reviews = "220 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "drink"
            ),
            FoodItemEntity(
                id = 11,
                name = "Es Krim Lava",
                description = "Es krim cokelat dengan saus lava panas di dalamnya",
                price = "Rp 15.000",
                rating = "4.7",
                reviews = "250 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "other"
            ),
            FoodItemEntity(
                id = 12,
                name = "Roti Bakar Matcha",
                description = "Roti bakar dengan selai matcha dan taburan keju",
                price = "Rp 12.000",
                rating = "4.5",
                reviews = "180 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "other"
            ),
            FoodItemEntity(
                id = 13,
                name = "Puding Rainbow",
                description = "Puding warna-warni dengan rasa buah segar",
                price = "Rp 10.000",
                rating = "4.6",
                reviews = "200 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "other"
            ),
            FoodItemEntity(
                id = 14,
                name = "Kue Cubit",
                description = "Kue cubit mini dengan topping cokelat dan meses",
                price = "Rp 8.000",
                rating = "4.4",
                reviews = "150 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = false,
                category = "other"
            ),
            FoodItemEntity(
                id = 15,
                name = "Donat Unicorn",
                description = "Donat berwarna cerah dengan glaze manis unik",
                price = "Rp 14.000",
                rating = "4.8",
                reviews = "230 Penilaian",
                imageRes = R.drawable.salez_logo,
                isPopular = true,
                category = "other"
            )
        )
        repository.insertFoodItems(foodItems)
    }
}