package com.example.myfinance.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myfinance.navigation.Graph
import com.example.myfinance.ui.components.PrimaryText

@Composable
fun AuthScreen(navController: NavHostController) {
    AuthScreenContent(onNavigateToLogin = {
        navController.navigate(Graph.Login)
    }, onNavigateToRegistration = {
        navController.navigate(Graph.Registration)
    }, onNavigateToResetPassword = {
        navController.navigate(Graph.ResetPassword)
    }, onBackClick = {
        navController.popBackStack()
    })
}

@Composable
private fun AuthScreenContent(
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(15.dp)

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.TwoTone.ChevronLeft,
                    contentDescription = "",
                    modifier = Modifier.clickable(true, onClick = { onBackClick() })
                )
                PrimaryText("Назад")
            }

        }
    }
}