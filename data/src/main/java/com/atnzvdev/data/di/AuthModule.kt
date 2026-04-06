package com.atnzvdev.data.di

import com.atnzvdev.data.repository.auth.AuthRepositoryImpl
import com.atnzvdev.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
    @Provides
    @Singleton
    fun provideFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}