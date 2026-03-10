package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.navigation.routes.AuthRoute
import com.example.myfinance.ui.MainFlowScreen
import com.example.myfinance.ui.profile.PatternScreen
import com.example.myfinance.ui.profile.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), modifier: Modifier
) {
    NavHost(
        navController = navController, startDestination = Graph.Main, modifier = modifier
    ) {
        AuthRoute(navController)

        composable(route = Graph.Main) {
            MainFlowScreen(navController)
        }
        composable(route = Graph.Settings) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Graph.Templates) {
            PatternScreen(
                onBackClick = { navController.popBackStack() })
        }
    }
}