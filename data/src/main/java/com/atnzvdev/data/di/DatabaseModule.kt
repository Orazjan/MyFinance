package com.atnzvdev.data.di

import com.atnzvdev.data.repository.templates.TemplateRepositoryImpl
import com.atnzvdev.data.repository.transactions.TransactionRepositoryImpl
import com.atnzvdev.data.repository.userPerfences.UserPreferencesRepositoryImpl
import com.atnzvdev.domain.repository.TemplateRepository
import com.atnzvdev.domain.repository.TransactionRepository
import com.atnzvdev.domain.repository.UserPreferencesRepository
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