package com.atnzvdev.domain.repository

import com.atnzvdev.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionById(id: Long)
    suspend fun deleteAllTransactions()
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>
}