package com.salez.kasir.di

import android.util.Log
import com.salez.kasir.data.repository.MenuRepository
import com.salez.kasir.data.repository.MenuRepository1
import com.salez.kasir.data.repository.OrderRepository
import com.salez.kasir.data.repository.OrderRepository1
import com.salez.kasir.data.repository.impl.MenuRepositoryImpl
import com.salez.kasir.data.repository.impl.OrderRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val TAG = "AppModule"

    @Provides
    @Singleton
    fun provideMenuRepository(): MenuRepository1 {
        Log.d(TAG, "Creating MenuRepository instance")
        return MenuRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideOrderRepository(): OrderRepository1 {
        Log.d(TAG, "Creating OrderRepository instance")
        return OrderRepositoryImpl()
    }
}