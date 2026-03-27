package com.example.myfinance.ui.profile.settings

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object OpenPrivacyPolicy : SettingsEvent
    data object OpenDeveloperEmail : SettingsEvent
    data object OpenVersionInfo : SettingsEvent
    data class ShowSnackBar(val message: String) : SettingsEvent
}