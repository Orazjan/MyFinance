package com.example.myfinance.data.repository.transactions

import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.mapper.toDomain
import com.example.myfinance.data.mapper.toEntity
import com.example.myfinance.domain.model.Transaction
import com.example.myfinance.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        return dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        return dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        return dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionById(id: Long) {
        return dao.deleteTransactionById(id)
    }

    override suspend fun deleteAllTransactions() {
        return dao.deleteAllTransactions()
    }

    override fun getTotalIncome(): Flow<Double> {
        return dao.getTotalIncome()
    }

    override fun getTotalExpense(): Flow<Double> {
        return dao.getTotalExpense()
    }


}