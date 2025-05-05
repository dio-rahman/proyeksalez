package com.main.proyek_salez.di

import android.content.Context
import androidx.room.Room
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.repository.CashierRepository
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
        )
            .fallbackToDestructiveMigration()
            .build()
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
    fun provideFoodDao(database: SalezDatabase): FoodDao {
        return database.FoodDao()
    }

    @Provides
    @Singleton
    fun provideSalezRepository(
        cartItemDao: CartItemDao,
        orderDao: OrderDao,
        foodDao: FoodDao
    ): CashierRepository {
        return CashierRepository(cartItemDao, foodDao, orderDao)
    }
}