package com.example.myfinance.domain.useCase

import com.example.myfinance.domain.model.FinanceSummary
import com.example.myfinance.domain.repository.TransactionRepository
import com.example.myfinance.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFinanceSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<FinanceSummary> {
        return combine(
            userPreferencesRepository.getBalance(),
            repository.getTotalIncome(),
            repository.getTotalExpense()
        ) { baseBalance, totalIncome, totalExpense ->
            FinanceSummary(
                baseBalance = baseBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                currentBalance = baseBalance + totalIncome - totalExpense
            )
        }
    }
}