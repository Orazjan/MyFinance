package com.atnzvdev.presentation.ui.profile.templates

import com.atnzvdev.domain.model.Template

data class TemplateUiState(
    val templates: List<Template> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)