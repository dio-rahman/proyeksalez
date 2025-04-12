package com.main.proyek_salez.ui.menu

import com.main.proyek_salez.R

object FoodData {
        val foodItems = listOf(
        FoodItem(
            id = 0, name = "Mi Goreng El Salvadore", description = "Olahan dengan nuansa tradisional yang berani tercipta dalam dan nuansa elegan",
            price = "Rp 12.000", rating = "4.5", reviews = "305 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 1, name = "Nasi Goreng Spesial", description = "Nasi goreng dengan campuran bumbu khas dan topping telur",
            price = "Rp 15.000", rating = "4.7", reviews = "420 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 2, name = "Ayam Bakar Madu", description = "Ayam bakar dengan balutan madu manis dan rempah pilihan",
            price = "Rp 20.000", rating = "4.8", reviews = "250 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 3, name = "Soto Ayam Lamongan", description = "Soto ayam khas Lamongan dengan kuah gurih dan suwiran ayam",
            price = "Rp 18.000", rating = "4.6", reviews = "180 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 4, name = "Bakso Beranak", description = "Bakso besar berisi bakso kecil dengan kuah kaldu sapi",
            price = "Rp 25.000", rating = "4.9", reviews = "350 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        )
    )
    val drinkItems = listOf(
        FoodItem(
            id = 0, name = "Jus Alpukat", description = "Jus alpukat segar dengan campuran susu dan gula alami",
            price = "Rp 10.000", rating = "4.5", reviews = "200 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 1, name = "Susu Cokelat", description = "Susu cokelat hangat dengan rasa manis yang pas",
            price = "Rp 8.000", rating = "4.3", reviews = "150 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 2, name = "Kopi Hitam", description = "Kopi hitam murni tanpa gula, pahit dan aromatik",
            price = "Rp 7.000", rating = "4.6", reviews = "180 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 3, name = "Teh Tarik", description = "Teh tarik khas dengan busa lembut dan rasa manis",
            price = "Rp 9.000", rating = "4.4", reviews = "160 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 4, name = "Es Kelapa Muda", description = "Kelapa muda segar dengan tambahan es batu",
            price = "Rp 12.000", rating = "4.8", reviews = "220 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        )
    )

    val otherItems = listOf(
        FoodItem(
            id = 0, name = "Es Krim Lava", description = "Es krim cokelat dengan saus lava panas di dalamnya",
            price = "Rp 15.000", rating = "4.7", reviews = "250 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 1, name = "Roti Bakar Matcha", description = "Roti bakar dengan selai matcha dan taburan keju",
            price = "Rp 12.000", rating = "4.5", reviews = "180 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 2, name = "Puding Rainbow", description = "Puding warna-warni dengan rasa buah segar",
            price = "Rp 10.000", rating = "4.6", reviews = "200 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        ),
        FoodItem(
            id = 3, name = "Kue Cubit", description = "Kue cubit mini dengan topping cokelat dan meses",
            price = "Rp 8.000", rating = "4.4", reviews = "150 Penilaian", imageRes = R.drawable.salez_logo, isPopular = false
        ),
        FoodItem(
            id = 4, name = "Donat Unicorn", description = "Donat berwarna cerah dengan glaze manis unik",
            price = "Rp 14.000", rating = "4.8", reviews = "230 Penilaian", imageRes = R.drawable.salez_logo, isPopular = true
        )
    )
}