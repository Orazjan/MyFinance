package com.example.myfinance.ui.auth

import android.util.Patterns
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
import androidx.compose.ui.unit.dp
import com.example.myfinance.ui.components.PrimaryButton
import com.example.myfinance.ui.components.PrimaryOutlinedTextField
import com.example.myfinance.ui.components.PrimaryText

@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit
) {
    val emailPattern = Patterns.EMAIL_ADDRESS
    var email by remember { mutableStateOf("") }
    val isError = email.isNotEmpty() && !emailPattern.matcher(email).matches()
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
                    "Вернём достуа", style = MaterialTheme.typography.headlineMedium
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Spacer(Modifier.height(5.dp))
                        PrimaryOutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            isError = isError,
                            errorMessage = "Некорректный формат почты",
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
                                        true, onClick = { email = "" })
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
                    }
                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        "Восстановить доступ",
                        onClick = { onBackClick() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}