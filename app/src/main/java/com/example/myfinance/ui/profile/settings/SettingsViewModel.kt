package com.example.myfinance.ui.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    fun settingsActions(actions: SettingsAction) {
        when (actions) {
            is SettingsAction.OnDeleteTemplatesClick ->
                onDeleteTemplatesClick()

            is SettingsAction.OnBackClick -> onBackClick()


            is SettingsAction.OnDeleteTransactionsClick ->
                onDeleteTransactionsClick()


            is SettingsAction.OnPrivacyPolicyClick ->
                onOpenPrivacyPolicyClick()


            is SettingsAction.OnWriteToDeveloperClick ->
                onWriteToDeveloperClick()

            is SettingsAction.OnVersionInfoClick -> onVersionInoClick()

        }
    }

    private fun onVersionInoClick() {
        emitEvent(SettingsEvent.OpenVersionInfo)
    }

    private fun onBackClick() {
        emitEvent(SettingsEvent.NavigateBack)
    }

    fun onDeleteTemplatesClick() {
        emitEvent(SettingsEvent.ShowSnackBar("Заглужка для удаления Шаблонов"))
    }

    fun onDeleteTransactionsClick() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
        }
    }

    fun onOpenPrivacyPolicyClick() {
        emitEvent(SettingsEvent.OpenPrivacyPolicy)
    }

    fun onWriteToDeveloperClick() {
        emitEvent(SettingsEvent.OpenDeveloperEmail)
    }

    private fun emitEvent(event: SettingsEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}