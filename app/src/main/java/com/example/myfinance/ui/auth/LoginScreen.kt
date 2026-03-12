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
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.Login
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.myfinance.R
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryCard
import com.example.myfinance.ui.components.PrimaryOutlinedTextField
import com.example.myfinance.ui.components.PrimaryText

@Composable
fun AuthScreen(
    navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AuthScreenContent(
        state = uiState,
        onEmailChange = { viewModel.onEmailChanged(it) },
        onPasswordChange = { viewModel.onPasswordChanged(it) },
        onLoginClick = {
            viewModel.login {
                navController.navigate(Graph.Main)
            }
        },
        onNavigateToRegistration = { navController.navigate(Graph.Registration) },
        onNavigateResetPassword = { navController.navigate(Graph.ResetPassword) },
        onBackClick = { navController.popBackStack() },

        )
}
@Composable
private fun AuthScreenContent(
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateResetPassword: () -> Unit,
    state: AuthUiState,
) {
    var passwordVisible by remember { mutableStateOf(false) }

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
                    Icons.TwoTone.ChevronLeft, contentDescription = stringResource(R.string.back),
                    modifier = Modifier.clickable(onClick = { onBackClick() })
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
                    "Сохраните свои данные", style = MaterialTheme.typography.headlineMedium
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
                                Icon(Icons.TwoTone.Login, contentDescription = "Email Icon")
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.TwoTone.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.clickable(onClick = { onEmailChange("") })
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrect = false,
                                capitalization = KeyboardCapitalization.None,
                                imeAction = ImeAction.Next
                            ),
                            shape = OutlinedTextFieldDefaults.shape
                        )

                        Spacer(Modifier.height(10.dp))

                        PrimaryOutlinedTextField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            isError = state.passwordError != null,
                            errorMessage = state.passwordError ?: "",
                            label = {
                                PrimaryText(
                                    text = "Пароль",
                                    color = MaterialTheme.colorScheme.primary.copy(0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.TwoTone.Lock, contentDescription = "Password Icon")
                            },
                            trailingIcon = {
                                val icon =
                                    if (passwordVisible) Icons.TwoTone.Visibility else Icons.TwoTone.VisibilityOff
                                val description =
                                    if (passwordVisible) "Скрыть пароль" else "Показать пароль"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = description)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                autoCorrect = false,
                                keyboardType = KeyboardType.Password,
                                capitalization = KeyboardCapitalization.None,
                                imeAction = ImeAction.Done
                            ),
                            shape = OutlinedTextFieldDefaults.shape,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            PrimaryText(
                                "Забыли пароль?",
                                modifier = Modifier.clickable(onClick = { onNavigateResetPassword() })
                            )
                        }

                        Spacer(Modifier.height(15.dp))

                        if (state.generalError != null) {
                            PrimaryText(
                                text = state.generalError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        PrimaryButton(
                            text = if (state.isLoading) "Загрузка..." else "Войти",
                            enabled = !state.isLoading && state.emailError == null && state.passwordError == null,
                            onClick = { onLoginClick() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrimaryText(
                    "Нет аккаунта? ",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable(onClick = { onNavigateToRegistration() })
                )
                PrimaryText(
                    "Создать",
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable(onClick = { onNavigateToRegistration() })
                )
            }
        }
    }
}