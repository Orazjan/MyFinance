package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.navigation.routes.AuthRoute
import com.example.myfinance.ui.MainFlowScreen
import com.example.myfinance.ui.main.transActions.AddTransActionScreen
import com.example.myfinance.ui.profile.settings.SettingsScreen
import com.example.myfinance.ui.profile.templates.AddTemplateScreen
import com.example.myfinance.ui.profile.templates.PatternScreen
import com.example.myfinance.ui.profile.versionOfApp.VersionInfoScreen


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
                navController
            )
        }
        composable(route = Graph.Templates) {
            PatternScreen(
                onBackClick = { navController.popBackStack() },
                goToAddTemplate = { navController.navigate(Graph.AddTemplate) }
            )
        }
        composable(route = Graph.AddTemplate) {
            AddTemplateScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Graph.AddTransAction) {
            AddTransActionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Graph.VersionOfApp) {
            VersionInfoScreen { navController.popBackStack() }

        }
    }
}