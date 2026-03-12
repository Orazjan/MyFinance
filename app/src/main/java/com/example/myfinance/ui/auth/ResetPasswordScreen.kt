package com.example.myfinance.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ChevronLeft
import androidx.compose.material.icons.twotone.Clear
import androidx.compose.material.icons.twotone.Login
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myfinance.R
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryOutlinedTextField
import com.example.myfinance.ui.components.PrimaryText

@Composable
fun ResetPasswordScreen(
    navController: NavHostController, viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ResetPasswordScreenContent(
        state = uiState,
        onResetClick = { viewModel.onResetClick { navController.navigate(Graph.Auth) } },
        onEmailChange = { viewModel.onLoginChange(it) },
        onBackClick = { navController.popBackStack() },
    )
}

@Composable
fun ResetPasswordScreenContent(
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    state: ResetPasswordUiState,
    onResetClick: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize()

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.TwoTone.ChevronLeft,
                    contentDescription = "",
                    modifier = Modifier.clickable(true, onClick = { onBackClick() })
                )
                PrimaryText(
                    stringResource(R.string.back),
                    modifier = Modifier.clickable(true, onClick = { onBackClick() })
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PrimaryText(
                    "Вернём доступ", style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(40.dp))
                PrimaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = 12.dp,
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(5.dp))

                        PrimaryOutlinedTextField(
                            value = state.email,
                            onValueChange = onEmailChange,
                            isError = state.emailError != null,
                            errorMessage = state.emailError ?: "",
                            label = {
                                PrimaryText(
                                    "Email", color = MaterialTheme.colorScheme.primary.copy(0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.TwoTone.Login, contentDescription = "Name"
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.TwoTone.Clear,
                                    contentDescription = "Name",
                                    modifier = Modifier.clickable(
                                        true, onClick = { onEmailChange("") })
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrect = false,
                                capitalization = KeyboardCapitalization.None,
                                imeAction = ImeAction.Done
                            ),
                            shape = OutlinedTextFieldDefaults.shape
                        )
                        Spacer(Modifier.height(10.dp))

                        LaunchedEffect(state.generalError) {
                            state.generalError?.let {
                                snackBarHostState.showSnackbar(it)
                            }
                        }

                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        text = if (state.isLoading) "Загрузка..." else "Восстановить доступ",
                        onClick = onResetClick,
                        enabled = !state.isLoading && state.emailError == null,
                        modifier = Modifier.fillMaxWidth()
                    )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}