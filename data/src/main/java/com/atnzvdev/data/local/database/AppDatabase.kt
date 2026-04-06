package com.atnzvdev.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atnzvdev.data.local.dao.TemplateDao
import com.atnzvdev.data.local.dao.TransactionDao
import com.atnzvdev.data.local.entity.TemplateEntity
import com.atnzvdev.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, TemplateEntity::class], version = 1, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun templateDao(): TemplateDao


}