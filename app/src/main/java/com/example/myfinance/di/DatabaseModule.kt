package com.example.myfinance.di

import com.example.myfinance.data.repository.templates.TemplateRepositoryImpl
import com.example.myfinance.data.repository.transactions.TransactionRepositoryImpl
import com.example.myfinance.data.repository.userPerfences.UserPreferencesRepositoryImpl
import com.example.myfinance.domain.repository.TemplateRepository
import com.example.myfinance.domain.repository.TransactionRepository
import com.example.myfinance.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindTemplateRepository(
        impl: TemplateRepositoryImpl
    ): TemplateRepository
}