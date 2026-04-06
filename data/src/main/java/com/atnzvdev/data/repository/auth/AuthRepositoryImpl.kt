package com.atnzvdev.data.repository.auth

import com.atnzvdev.data.mapper.toDomain
import com.atnzvdev.domain.model.User
import com.atnzvdev.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun login(
        email: String, password: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Result.success(Unit)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun registration(
        email: String, password: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                auth.sendPasswordResetEmail(email).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                val firebaseUser = auth.currentUser
                val domainUser = firebaseUser?.toDomain()
                Result.success(domainUser)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun logOut(): Result<Unit> {
        return withContext(Dispatchers.IO) { Result.success(auth.signOut()) }
    }
}