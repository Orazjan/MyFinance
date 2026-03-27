package com.example.myfinance.ui.profile.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CurrencyExchange
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.DeleteForever
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Security
import androidx.compose.material.icons.twotone.Send
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.components.DestructiveCard
import com.example.myfinance.ui.components.PrimaryLazyColumn
import com.example.myfinance.ui.components.PrimarySpinner
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.ClickableRow
import com.example.myfinance.ui.components.SectionHeader
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun SettingsScreen(
    navHostController: NavHostController, viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.events.collect { events ->
            when (events) {
                is SettingsEvent.OpenPrivacyPolicy -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/view/myfinanceteam/")
                    )
                    context.startActivity(intent)
                }

                is SettingsEvent.OpenDeveloperEmail -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:atnzvdev@gmail.com")
                    }
                    context.startActivity(intent)
                }

                is SettingsEvent.OpenVersionInfo -> navHostController.navigate(Graph.VersionOfApp)
                is SettingsEvent.NavigateBack -> navHostController.popBackStack()
                is SettingsEvent.ShowSnackBar -> snackbarHostState.showSnackbar(events.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        TopNavBar(
            title = "Настройки",
            onBackClick = { viewModel.settingsActions(SettingsAction.OnBackClick) })
    }) { innerPadding ->
        SettingsScreenContent(
            state = state,
            onWriteToDeveloper = { viewModel.settingsActions(SettingsAction.OnWriteToDeveloperClick) },
            onDeleteTemplate = { viewModel.settingsActions(SettingsAction.OnDeleteTemplatesClick) },
            onDeleteTransActions = { viewModel.settingsActions(SettingsAction.OnDeleteTransactionsClick) },
            onNavigateToPolicyConfidency = { viewModel.settingsActions(SettingsAction.OnPrivacyPolicyClick) },
            onNavigateToVersionInfo = { viewModel.settingsActions(SettingsAction.OnVersionInfoClick) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Composable
fun SettingsScreenContent(
    state: SettingsUiState,
    modifier: Modifier,
    onWriteToDeveloper: () -> Unit,
    onDeleteTemplate: () -> Unit,
    onNavigateToPolicyConfidency: () -> Unit,
    onNavigateToVersionInfo: () -> Unit,
    onDeleteTransActions: () -> Unit

) {
    val currencies = listOf("USD ($)", "EUR (€)", "RUB (₽)", "KGS (с)")
    val themes = listOf("Светлая", "Темная", "Системная")

    PrimaryLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionHeader(icon = Icons.TwoTone.CurrencyExchange, title = "Валюта")
            PrimarySpinner(
                options = currencies,
                selectedOption = state.currency,
                onOptionSelected = { it },
                label = "Выберите валюту",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        item {
            SectionHeader(icon = Icons.TwoTone.Visibility, title = "Тема приложения")
            PrimarySpinner(
                options = themes,
                selectedOption = if (state.isDarkTheme) "Темная" else "Светлая",
                onOptionSelected = { it },
                label = "Выберите тему",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        item {
            PrimaryText(
                text = "О приложении",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.padding(top = 8.dp)) {
                ClickableRow(
                    icon = Icons.TwoTone.Send,
                    title = "Написать разработчику",
                    onClick = onWriteToDeveloper
                )
                ClickableRow(
                    icon = Icons.TwoTone.Security,
                    title = "Политика конфиденциальности",
                    onClick = onNavigateToPolicyConfidency
                )
                ClickableRow(
                    icon = Icons.TwoTone.Star,
                    title = "Оценить приложение",
                    onClick = { /* TODO */ }
                )
                ClickableRow(
                    icon = Icons.TwoTone.Info, title = "Версия приложения: 0.9.6",
                    onClick = onNavigateToVersionInfo
                )
            }
        }

        item {
            PrimaryText(
                text = "Управление данными",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DestructiveCard(
                title = "Удалить шаблоны",
                subtitle = "Очистить все сохраненные шаблоны",
                icon = Icons.TwoTone.Delete,
                isLoading = state.isDeletingTemplates,
                onClick = onDeleteTemplate
            )

            Spacer(modifier = Modifier.height(12.dp))

            DestructiveCard(
                title = "Удалить финансы",
                subtitle = "Стереть историю операций",
                icon = Icons.TwoTone.DeleteForever,
                isLoading = state.isDeletingTransactions,
                onClick = onDeleteTransActions
            )

        }

}
}
