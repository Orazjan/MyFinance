package com.atnzvdev.domain.useCase

import com.atnzvdev.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateCurrentBalanceUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        newCurrentBalance: Double,
        totalIncome: Double,
        totalExpense: Double
    ) {
        val newBaseBalance = newCurrentBalance - totalIncome + totalExpense
        userPreferencesRepository.setBalance(newBaseBalance)
    }
}