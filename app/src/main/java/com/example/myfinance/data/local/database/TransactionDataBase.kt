package com.example.myfinance.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class TransactionDataBase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}