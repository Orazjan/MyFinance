package com.example.myfinance.domain.repository

import com.example.myfinance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun getTransactionById(id: Long): TransactionEntity?
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(transaction: TransactionEntity)
    suspend fun deleteTransactionById(id: Long)
    suspend fun deleteAllTransactions()
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>
}