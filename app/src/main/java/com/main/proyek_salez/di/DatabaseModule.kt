package com.main.proyek_salez.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.main.proyek_salez.data.dao.CartItemDao
import com.main.proyek_salez.data.dao.FoodDao
import com.main.proyek_salez.data.dao.OrderDao
import com.main.proyek_salez.data.repository.CashierRepository
import com.main.proyek_salez.data.repository.ManagerRepository
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
    fun provideFoodDao(database: SalezDatabase): FoodDao {
        return database.FoodDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(database: SalezDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideCashierRepository(
        firestore: FirebaseFirestore,
        cartItemDao: CartItemDao,
        foodDao: FoodDao,
        orderDao: OrderDao
    ): CashierRepository {
        return CashierRepository(firestore, cartItemDao, foodDao, orderDao)
    }

    @Provides
    @Singleton
    fun provideManagerRepository(
        firestore: FirebaseFirestore
    ): ManagerRepository {
        return ManagerRepository(firestore)
    }
}