package com.example.myfinance.data

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

}