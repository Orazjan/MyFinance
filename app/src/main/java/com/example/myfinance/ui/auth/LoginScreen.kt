package com.example.myfinance.ui.auth

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryOutlinedTextField
import com.example.myfinance.ui.components.PrimaryText

@Composable
fun AuthScreen(
    navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()
) {
    AuthScreenContent(
        email = viewModel.email,
        password = viewModel.password,
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
        emailError = viewModel.emailError,
        passwordError = viewModel.passwordError,
        generalError = viewModel.generalError,
        isLoading = viewModel.isLoading
    )
}

@Composable
private fun AuthScreenContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateResetPassword: () -> Unit,
    emailError: String?,
    passwordError: String?,
    generalError: String?,
    isLoading: Boolean
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
                    Icons.TwoTone.ChevronLeft,
                    contentDescription = "",
                    modifier = Modifier.clickable(true, onClick = { onBackClick() })
                )
                PrimaryText("Назад")
            }


            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryText(
                    "Сохраните свои данные", style = MaterialTheme.typography.headlineMedium
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(

                    ) {
                        Spacer(Modifier.height(5.dp))
                        PrimaryOutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            isError = emailError != null,
                            errorMessage = emailError ?: "",
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
                                imeAction = ImeAction.Next
                            ),
                            shape = OutlinedTextFieldDefaults.shape
                        )
                        Spacer(Modifier.height(5.dp))
                        PrimaryOutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            isError = passwordError != null,
                            errorMessage = passwordError ?: "",
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            PrimaryText(
                                "Забыли пароль?", modifier = Modifier.clickable(
                                    true, onClick = { onNavigateResetPassword() })
                            )
                        }
                    }


                    Spacer(Modifier.height(20.dp))
                    if (generalError != null) {
                        PrimaryText(
                            text = generalError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        text = if (isLoading) "Загрузка..." else "Войти",
                        enabled =
                            !isLoading && emailError == null && passwordError == null,
                        onClick = { onLoginClick() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        "Зарегестрироваться",
                        onClick = { onNavigateToRegistration() },
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        }
    }
}