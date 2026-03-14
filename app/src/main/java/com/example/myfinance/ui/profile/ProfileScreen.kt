package com.example.myfinance.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Style
import androidx.compose.material.icons.twotone.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryText
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
                .padding(top = 50.dp)
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
                            style = MaterialTheme.typography.displayMedium
                        )
                        PrimaryText(
                            text = state.email,
                            modifier = Modifier.alpha(0.5f),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.height(15.dp))
                        PrimaryCard {
                            PrimaryText(
                                text = state.plan,
                                modifier = Modifier
                                    .alpha(0.5f)
                                    .padding(5.dp),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
                    .padding(
                        start = 16.dp, end = 16.dp, top = 20.dp, bottom = 100.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                PrimaryButton(
                    "", onClick = { goToSettings() }, modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.TwoTone.Settings,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        PrimaryText(
                            "Настройки",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            Icons.TwoTone.ChevronRight,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    "", onClick = { goToPattern() }, modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.TwoTone.Style,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        PrimaryText(
                            "Готовые шаблоны",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            Icons.TwoTone.ChevronRight,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    "", onClick = { goToPattern() }, modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.TwoTone.Sync,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        PrimaryText(
                            "Синхронизация",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            Icons.TwoTone.ChevronRight,
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    state.textForAuthButton,
                    onClick = { if (state.isAuth == true) logOut() else goToAuth() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.TwoTone.Person, contentDescription = "")
                }
            }
        }
    }
}