package com.example.myfinance.data

import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}