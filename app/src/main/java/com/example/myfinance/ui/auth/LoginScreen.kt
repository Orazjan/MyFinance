package com.example.myfinance.ui.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun LoginScreen(onBackNavigation: () -> Unit) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Логин", onBackClick = { onBackNavigation() })
        }) { innerPadding ->

        Text("Логин страница", modifier = Modifier.padding(innerPadding))

    }
}