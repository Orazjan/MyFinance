package com.example.myfinance.ui.profile.templates

import com.example.myfinance.domain.model.Templates

sealed interface TemplatesUiState {
    object Loading : TemplatesUiState
    data class Success(val list: List<Templates>) : TemplatesUiState
    data class Failure(val message: String) : TemplatesUiState
}