package com.main.proyek_salez.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.main.proyek_salez.data.repository.CashierRepository
import com.main.proyek_salez.data.repository.ManagerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideManagerRepository(firestore: FirebaseFirestore): ManagerRepository {
        return ManagerRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideCashierRepository(
        firestore: FirebaseFirestore,
        managerRepository: ManagerRepository
    ): CashierRepository {
        return CashierRepository(firestore, managerRepository)
    }
}