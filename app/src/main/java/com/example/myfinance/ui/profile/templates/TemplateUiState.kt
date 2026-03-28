package com.example.myfinance.ui.profile.templates

import com.example.myfinance.data.local.entity.TemplateEntity

data class TemplateUiState(
    val templates: List<TemplateEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)