package com.atnzvdev.presentation.ui.profile.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atnzvdev.domain.model.TypeOfOperation
import com.atnzvdev.presentation.ui.components.PrimaryButton
import com.atnzvdev.presentation.ui.components.PrimaryCard
import com.atnzvdev.presentation.ui.components.PrimaryLazyColumn
import com.atnzvdev.presentation.ui.components.PrimaryText
import com.atnzvdev.presentation.ui.components.TopNavBar

@Composable
fun TemplateScreen(
    onBackClick: () -> Unit,
    goToAddTemplate: () -> Unit,
    viewModel: TemplatesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is TemplateEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is TemplateEvent.NavigateBack -> onBackClick()
                is TemplateEvent.NavigateToAddTemplate -> goToAddTemplate()
            }
        }
    }

    Scaffold(topBar = {
        TopNavBar(
            title = "Шаблоны",
            onBackClick = { onBackClick() },
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, floatingActionButton = {
        FloatingActionButton(
            onClick = { goToAddTemplate() },
            modifier = Modifier.padding(bottom = 10.dp, end = 10.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = CircleShape
        ) {
            Icon(Icons.TwoTone.Add, contentDescription = "Добавить")
        }
    }) { innerpadding ->
        TemplateScreenContent(
            state = state,
            modifier = Modifier.padding(innerpadding),
            goToAddTemplate = goToAddTemplate
        )
    }
}

@Composable
fun TemplateScreenContent(modifier: Modifier, goToAddTemplate: () -> Unit, state: TemplateUiState) {

    Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryText(
                    text = "Чтобы каждый раз не писать, можете создать шаблон и использовать его или изменить при желании",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface,
                )
                PrimaryButton(
                    onClick = goToAddTemplate,
                    containerColor = MaterialTheme.colorScheme.surface,
                    text = "Создать",
                    enabled = true,
                    shape = 8.dp,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(10.dp))

        PrimaryText("Сохранённые шаблоны", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(15.dp))
        PrimaryLazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(20) { entry ->
                PrimaryCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Fastfood, contentDescription = ""
                        )
                        Row(
                            modifier = Modifier
                                .weight(2f)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrimaryText(entry.toString())
                            Column {
                                PrimaryText(
                                    "Другое", style = MaterialTheme.typography.titleMedium
                                )
                                PrimaryText(
                                    text = TypeOfOperation.entries[1].nameOfType,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.error
                                )

                            }
                            PrimaryText("200")
                        }
                    }
                }
            }
        }
    }
}