package com.atnzvdev.domain.repository

import com.atnzvdev.domain.model.User


interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun registration(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun logOut(): Result<Unit>
}