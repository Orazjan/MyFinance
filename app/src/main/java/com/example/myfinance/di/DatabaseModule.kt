package com.example.myfinance.di

import android.content.Context
import androidx.room.Room
import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.local.database.TransactionDataBase
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TransactionDataBase {
        return Room.databaseBuilder(
            context = context, klass = TransactionDataBase::class.java, name = "my_finance_db"
        ).build()

    }

    @Provides
    fun provideTransactionDao(database: TransactionDataBase): TransactionDao {
        return database.transactionDao()
    }
}