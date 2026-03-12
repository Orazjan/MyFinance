package com.example.myfinance.di

import com.example.myfinance.data.AuthRepository
import com.example.myfinance.data.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }
}