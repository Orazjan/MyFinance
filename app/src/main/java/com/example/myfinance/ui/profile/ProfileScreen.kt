package com.example.myfinance.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.components.TopNavBar

@Composable
fun ProfileScreen(onGoToMain: () -> Unit) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Профиль",
                onBackClick = { onGoToMain() }
            )
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {}
    }
}