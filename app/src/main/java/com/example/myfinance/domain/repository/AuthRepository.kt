package com.example.myfinance.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun registration(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
}