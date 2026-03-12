package com.example.myfinance.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    override suspend fun login(
        email: String, password: String
    ): Result<Unit> {
        return if (email == "test@test.com" && password == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Неверно"))
        }
    }

    override suspend fun registration(
        email: String, password: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            if (email == "test@test.com" && password == "12345678") {
                try {
                    delay(2000)
                    Result.success(Unit)
                } catch (e: Exception) {
                    Result.failure(Exception("Ошибка сети"))
                }
            } else {
                Result.failure(Exception("Ошибка сети"))
            }
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return Result.success(Unit)
    }

}