package com.example.myfinance.data.repository.userPerfences

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.myfinance.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {
    companion object {
        private val BASE_BALANCE_KEY = doublePreferencesKey("base_balance")
    }

    override fun getBalance(): Flow<Double> {
        return context.dataStore.data.map { preferences ->
            preferences[BASE_BALANCE_KEY] ?: 0.0
        }
    }

    override suspend fun setBalance(balance: Double) {
        context.dataStore.edit { preferences ->
            preferences[BASE_BALANCE_KEY] = balance
        }
    }
}