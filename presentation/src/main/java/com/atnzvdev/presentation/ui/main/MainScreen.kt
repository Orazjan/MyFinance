package com.atnzvdev.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.icons.twotone.Payments
import androidx.compose.material.icons.twotone.RemoveCircleOutline
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atnzvdev.domain.model.Months
import com.atnzvdev.domain.model.TypeOfOperation
import com.atnzvdev.presentation.ui.components.PrimaryCard
import com.atnzvdev.presentation.ui.components.PrimaryLazyColumn
import com.atnzvdev.presentation.ui.components.PrimarySpinner
import com.atnzvdev.presentation.ui.components.PrimaryText
import com.atnzvdev.presentation.ui.components.TopNavBar

@Composable
fun MainScreen(
    onNavigateToAddTransaction: () -> Unit,
//    scrollState: LazyListState,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { events ->
            when (events) {
                MainEvent.NavigateToAddTransAction -> onNavigateToAddTransaction()
                is MainEvent.ShowSnackbar -> snackbarHostState.showSnackbar(events.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "My Finance",
                onBackClick = null,
                modifier = Modifier
                    .padding(0.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAction(MainAction.OnAddTransactionClick) },
                modifier = Modifier.padding(bottom = 80.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape,
            ) {
                Icon(Icons.TwoTone.Add, contentDescription = "Добавить")
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
    ) { innerPadding ->
        MainScreenContent(
            haptic = haptic,
            modifier = Modifier.padding(innerPadding),
            state = uiState,
            onAction = { action -> viewModel.onAction(action) }
        )
    }
}

@Composable
fun MainScreenContent(
    state: MainUiState,
    modifier: Modifier, onAction: (MainAction) -> Unit, haptic: HapticFeedback
) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                PrimaryText(
                    "Общий баланс",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {

                    PrimaryText(
                        state.totalBalance,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PrimaryText(
                        "$",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            PrimaryCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                elevation = (2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.RemoveCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFBA1A1A)
                            )
                            Spacer(Modifier.width(8.dp))
                            PrimaryText(
                                text = "Всего расходов",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Text(
                                text = state.totalExpense,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "$",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.TwoTone.Payments,
                        contentDescription = "",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                PrimarySpinner(
                    options = TypeOfOperation.entries.map { it.nameOfType },
                    selectedOption = state.selectedType.nameOfType,
                    onOptionSelected = { selectedName ->
                        val selectedType =
                            TypeOfOperation.entries.find { it.nameOfType == selectedName }
                        selectedType?.let {
                            onAction(MainAction.OnTypeSelected(it))
                        }
                    },
                    label = "Общее",
                    modifier = Modifier
                        .weight(1f)
                )

                PrimarySpinner(
                    options = Months.entries.map { it.displayName },
                    selectedOption = state.selectedMonth.displayName,
                    onOptionSelected = { selectedMonth ->
                        val selectedMonth = Months.entries.find { it.displayName == selectedMonth }
                        selectedMonth?.let {
                            onAction(MainAction.OnMonthSelected(it))
                        }
                    },
                    label = "Месяцы",
                    modifier = Modifier
                        .weight(1f)
                )
            }

            PrimaryLazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.transactions) { transaction ->
                    PrimaryCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(enabled = true, onClick = {
                                onAction(
                                    MainAction.OnShowDialogAlertClick
                                )
                            }, onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAction(MainAction.OnDeleteClick)
                            })

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.TwoTone.Image,
                                contentDescription = "",
                                modifier = Modifier
                                    .clip(
                                        CircleShape
                                    )
                                    .size(50.dp)
                                    .padding(end = 10.dp)
                            )
                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                PrimaryText(
                                    text = transaction.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                val typeLabel =
                                    TypeOfOperation.fromDisplayName(transaction.type.nameOfType)?.nameOfType
                                        ?: "Неизвестно"
                                PrimaryText(
                                    text = typeLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                PrimaryText(
                                    text = "${transaction.amount} $",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (transaction.type.nameOfType == "Доходы") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

