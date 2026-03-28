package com.example.myfinance.ui.profile.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.domain.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val repository: TemplateRepository
) : ViewModel() {
    private val _navigation = MutableSharedFlow<TemplateEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        onLoad()
    }

    private val _uiState = MutableStateFlow(TemplateUiState())
    val uiState: StateFlow<TemplateUiState> = _uiState.asStateFlow()

    fun onEvent(action: TemplateAction) {
        when (action) {
            is TemplateAction.OnAddTransactionClick -> onNavigateToAddTransaction()
            is TemplateAction.OnBackClick -> onNavigateBack()
            is TemplateAction.OnChangeClick -> onChangeClick()
            is TemplateAction.OnDeleteClick -> onDeleteClick()

        }
    }

    private fun onDeleteClick() {
        TODO("Not yet implemented")
    }

    private fun onChangeClick() {
        TODO()
    }

    private fun onNavigateBack() {
        emit(TemplateEvent.NavigateBack)
    }

    private fun onNavigateToAddTransaction() {
        emit(TemplateEvent.NavigateToAddTemplate)
    }

    private fun onLoad() {
        viewModelScope.launch {
            repository.getAllTemplates().collect { templates ->
                _uiState.update {
                    it.copy(
                        templates = templates
                    )
                }
            }
        }
    }

    fun emit(templateEvent: TemplateEvent) {
        viewModelScope.launch {
            _navigation.emit(templateEvent)
        }
    }
}