package com.atnzvdev.presentation.ui.profile.settings

data class SettingsUiState(
    val currency: String = "$",
    val isDarkTheme: Boolean = false,
    val isDeletingTemplates: Boolean = false,
    val isDeletingTransactions: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)