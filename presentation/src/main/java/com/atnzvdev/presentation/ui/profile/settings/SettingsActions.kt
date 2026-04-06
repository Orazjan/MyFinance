package com.atnzvdev.presentation.ui.profile.settings

sealed interface SettingsAction {
    data object OnBackClick : SettingsAction
    data object OnDeleteTemplatesClick : SettingsAction
    data object OnDeleteTransactionsClick : SettingsAction
    data object OnPrivacyPolicyClick : SettingsAction
    data object OnWriteToDeveloperClick : SettingsAction
    data object OnVersionInfoClick : SettingsAction
}
