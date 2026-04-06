package com.atnzvdev.data.di

import android.content.Context
import androidx.room.Room
import com.atnzvdev.data.local.dao.TemplateDao
import com.atnzvdev.data.local.dao.TransactionDao
import com.atnzvdev.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TransactionModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context, klass = AppDatabase::class.java, name = "my_finance_db"
        ).fallbackToDestructiveMigration(false).build()

    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    fun provideTemplateDao(database: AppDatabase): TemplateDao {
        return database.templateDao()
    }
}