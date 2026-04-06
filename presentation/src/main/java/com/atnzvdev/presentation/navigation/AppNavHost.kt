package com.atnzvdev.presentation.navigation

import AppDestination
import Graph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atnzvdev.presentation.navigation.routes.AuthRoute
import com.atnzvdev.presentation.ui.MainFlowScreen
import com.atnzvdev.presentation.ui.main.transactions.AddTransActionScreen
import com.atnzvdev.presentation.ui.profile.settings.SettingsScreen
import com.atnzvdev.presentation.ui.profile.templates.TemplateScreen
import com.atnzvdev.presentation.ui.profile.templates.addTemplate.AddTemplateScreen
import com.atnzvdev.presentation.ui.profile.versionOfApp.VersionInfoScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), modifier: Modifier
) {
    NavHost(
        navController = navController, startDestination = Graph.Auth, modifier = modifier
    ) {
        AuthRoute(navController)

        composable<Graph.Main> {
            MainFlowScreen(navController)
        }

        composable<AppDestination.Settings> {
            SettingsScreen(
                navController
            )
        }
        composable<AppDestination.Templates> {
            TemplateScreen(
                onBackClick = { navController.popBackStack() },
                goToAddTemplate = { navController.navigate(AppDestination.AddTemplate) }
            )
        }
        composable<AppDestination.AddTemplate> {
            AddTemplateScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<AppDestination.AddTransaction> {
            AddTransActionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<AppDestination.VersionOfApp> {
            VersionInfoScreen { navController.popBackStack() }

        }
    }
}