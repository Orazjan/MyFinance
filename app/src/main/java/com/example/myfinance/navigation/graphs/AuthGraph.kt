package com.example.myfinance.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.myfinance.ui.auth.RegistrationScreen
import com.example.myfinance.navigation.Graph
import com.example.myfinance.navigation.Routes

fun NavGraphBuilder.Auth(navController: NavController) {
    navigation<Graph.Auth>(startDestination = Routes.RegRoute) {
        composable<Routes.RegRoute> {
            RegistrationScreen(
                onBackNavigation = { navController.navigate(Routes.LoginRoute) })
        }
    }
}