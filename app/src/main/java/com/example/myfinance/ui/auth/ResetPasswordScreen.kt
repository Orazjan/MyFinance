package com.example.myfinance.ui.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.components.PrimaryText
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Восстановление пароля",
                onBackClick = { onBackClick() }
            )
        }) { innerPadding ->
        PrimaryText("Восстановление пароля", modifier = Modifier.padding(innerPadding))
    }
}