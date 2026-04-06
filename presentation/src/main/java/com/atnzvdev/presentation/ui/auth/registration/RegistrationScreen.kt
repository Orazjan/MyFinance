package com.atnzvdev.presentation.ui.auth.registration

import Graph
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
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.atnzvdev.presentation.R
import com.atnzvdev.presentation.ui.components.PrimaryButton
import com.atnzvdev.presentation.ui.components.PrimaryCard
import com.atnzvdev.presentation.ui.components.PrimaryOutlinedTextField
import com.atnzvdev.presentation.ui.components.PrimaryText

@Composable
fun RegistrationScreen(
    navController: NavHostController, viewModel: RegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->

            when (event) {
                is RegistrationEvents.NavigateToMain -> {
                    navController.navigate(MainDestinations.Home)
                }

                is RegistrationEvents.NavigateToLogin -> {
                    navController.navigate(AuthDestination.Login)

                }

                is RegistrationEvents.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
    RegistrationScreenContent(
        modifier = Modifier.padding(innerPadding),
        state = uiState,
        onEmailChange = viewModel::onChangeEmail,
        onPasswordChange = viewModel::onChangePassword,
        onNameChange = viewModel::onChangeUserName,
        onRegClick = viewModel::onRegClick,
        onLoginNavigation = viewModel::onLoginClick,
        onBackNavigation = navController::popBackStack
    )
    }
}

@Composable
fun RegistrationScreenContent(
    state: RegistrationUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegClick: () -> Unit,
    onBackNavigation: () -> Unit,
    onLoginNavigation: () -> Unit,
    modifier: Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.TwoTone.ChevronLeft,
                    contentDescription = "",
                    modifier = Modifier.clickable(true, onClick = { onBackNavigation() })
                )
                PrimaryText(
                    stringResource(R.string.back),
                    modifier = Modifier.clickable(true, onClick = { onBackNavigation() })
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
                        Spacer(Modifier.height(5.dp))
                        PrimaryOutlinedTextField(
                            value = state.userName,
                            onValueChange = onNameChange,
                            isError = state.userNameError != null,
                            errorMessage = state.userNameError ?: "",
                            label = {
                                PrimaryText(
                                    "Имя", color = MaterialTheme.colorScheme.primary.copy(0.5f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.TwoTone.Person, contentDescription = "Name"
                                )
                            },
                            trailingIcon = {
                                if (state.userName.isNotEmpty()) {
                                    Icon(
                                        Icons.TwoTone.Clear,
                                        contentDescription = "Name", modifier = Modifier.clickable(
                                            true, onClick = { onNameChange("") })
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                autoCorrect = false,
                                capitalization = KeyboardCapitalization.Words,
                                showKeyboardOnFocus = true,
                                imeAction = ImeAction.Next
                            ),
                            shape = OutlinedTextFieldDefaults.shape
                        )
                        Spacer(Modifier.height(10.dp))

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
                                if (state.email.isNotEmpty()) {
                                    Icon(
                                        Icons.TwoTone.Clear, contentDescription = "Clear",
                                        modifier = Modifier.clickable(
                                            true, onClick = { onEmailChange("") })
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                autoCorrect = false,
                                keyboardType = KeyboardType.Email,
                                capitalization = KeyboardCapitalization.None,
                                showKeyboardOnFocus = true,
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
                                Icon(
                                    Icons.TwoTone.Lock, contentDescription = "Password Icon"
                                )
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
                            text = if (state.isLoading) "Загрузка..." else "Зарегистрироваться",
                            onClick = onRegClick,
                            enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank() && state.userName.isNotBlank() && state.emailError == null && state.userNameError == null && state.passwordError == null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            PrimaryText(
                "Есть аккаунт? ",
                style = MaterialTheme.typography.titleMedium
            )
            PrimaryText(
                "Войти",
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable(
                    onClick = onLoginNavigation
                )
            )
        }
    }
}