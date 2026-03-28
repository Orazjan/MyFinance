package com.example.myfinance.ui.profile.templates.addTemplate

import androidx.lifecycle.ViewModel
import com.example.myfinance.domain.model.TypeOfOperation
import com.example.myfinance.domain.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddTemplateViewModel @Inject constructor(
    private val repository: TemplateRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTemplateUiState())
    val uiState: StateFlow<AddTemplateUiState> = _uiState.asStateFlow()

    fun onAction(action: AddTemplateAction) {
        when (action) {
            is AddTemplateAction.OnBackClick -> onBackClick()
            is AddTemplateAction.OnSaveClick -> onSaveClick()
            is AddTemplateAction.OnTypeSelected -> onSelectedType(action.type)
            is AddTemplateAction.OnNameChanged -> onNameChanged(action.name)
            is AddTemplateAction.OnAmountChanged -> onAmountChanged(action.amount)
        }
    }

    private fun onAmountChanged(amount: String) {
        TODO("Not yet implemented")
    }

    private fun onNameChanged(name: String) {
        TODO("Not yet implemented")
    }

    private fun onSelectedType(type: TypeOfOperation) {
        TODO()
    }

    private fun onSaveClick() {
        TODO("Not yet implemented")
    }

    private fun onBackClick() {
        TODO("Not yet implemented")
    }

}