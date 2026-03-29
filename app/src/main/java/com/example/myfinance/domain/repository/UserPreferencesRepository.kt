package com.example.myfinance.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getBalance(): Flow<Double>
    suspend fun setBalance(balance: Double)
}