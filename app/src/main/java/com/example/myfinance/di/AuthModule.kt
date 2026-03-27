package com.example.myfinance.di

import com.example.myfinance.data.repository.auth.AuthRepositoryImpl
import com.example.myfinance.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun ProvideFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}