package com.main.proyek_salez.data

import android.content.Context
import androidx.room.Room
import com.main.proyek_salez.data.SalezDatabase
import com.main.proyek_salez.data.SalezRepository
import com.main.proyek_salez.data.CartItemDao
import com.main.proyek_salez.data.FoodItemDao
import com.main.proyek_salez.data.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SalezDatabase {
        return Room.databaseBuilder(
            context,
            SalezDatabase::class.java,
            "salez_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFoodItemDao(database: SalezDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    @Singleton
    fun provideCartItemDao(database: SalezDatabase): CartItemDao {
        return database.cartItemDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(database: SalezDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideSalezRepository(
        foodItemDao: FoodItemDao,
        cartItemDao: CartItemDao,
        orderDao: OrderDao
    ): SalezRepository {
        return SalezRepository(foodItemDao, cartItemDao, orderDao)
    }
}