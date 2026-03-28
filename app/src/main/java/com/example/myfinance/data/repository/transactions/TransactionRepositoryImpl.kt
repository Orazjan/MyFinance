package com.example.myfinance.data.repository.transactions

import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return dao.getAllTransactions()
    }

    override suspend fun getTransactionById(id: Long): TransactionEntity? {
        return dao.getTransactionById(id)
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        return dao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        return dao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        return dao.deleteTransaction(transaction)
    }

    override suspend fun deleteTransactionById(id: Long) {
        return dao.deleteTransactionById(id)
    }

    override suspend fun deleteAllTransactions() {
        return dao.deleteAllTransactions()
    }

}