package com.atnzvdev.domain.useCase

import com.atnzvdev.domain.model.FinanceSummary
import com.atnzvdev.domain.repository.TransactionRepository
import com.atnzvdev.domain.repository.UserPreferencesRepository
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