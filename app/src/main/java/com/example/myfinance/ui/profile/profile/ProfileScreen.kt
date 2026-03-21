package com.example.myfinance.ui.profile.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Style
import androidx.compose.material.icons.twotone.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.ProfileButton
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onGoToMain: () -> Unit,
    goToPattern: () -> Unit,
    goToSettings: () -> Unit,
    goToAuth: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreenContent(
        state = uiState,
        onGoToMain = { onGoToMain() },
        goToPattern = { goToPattern() },
        goToSettings = { goToSettings() },
        logOut = { viewModel.logOut() },
        goToAuth = { goToAuth() },
    )
}

@Composable
fun ProfileScreenContent(
    onGoToMain: () -> Unit,
    goToPattern: () -> Unit,
    goToSettings: () -> Unit,
    goToAuth: () -> Unit,
    logOut: () -> Unit,
    state: ProfileUiState,
) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Профиль",
                onBackClick = { onGoToMain() }
            )
        }) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PrimaryText(
                            text = state.userName,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        PrimaryText(
                            text = state.email,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,

                        )
                        Spacer(Modifier.height(15.dp))
                        PrimaryCard(Modifier.background(MaterialTheme.colorScheme.surface)) {
                            PrimaryText(
                                text = state.plan,
                                modifier = Modifier
                                    .padding(5.dp),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),

                            )
                        }
                    }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Создайте аккаунт для синхронизации ваших данных",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = goToAuth,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Создать")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileButton(
                        Icons.TwoTone.Settings, "Настройки", { goToSettings() })
                }
                item {
                    ProfileButton(
                        Icons.TwoTone.Style, "Готовые шаблоны", { goToPattern() })
                }
                item {
                    ProfileButton(
                        Icons.TwoTone.Sync, "Синхронизация", { })
                }
                item {
                    ProfileButton(
                        Icons.TwoTone.Person,
                        state.textForAuthButton,
                        onClick = { if (state.isAuth == true) logOut() else goToAuth() },
                        null
                    )

                }
            }
        }
    }
}
