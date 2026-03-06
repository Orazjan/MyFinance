package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.MainFlowScreen
import com.example.myfinance.navigation.routes.AuthRoute

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), modifier: Modifier
) {

    NavHost(
        navController = navController, startDestination = Graph.Main, modifier = modifier
    ) {
        AuthRoute(navController)

        composable("main_graph") {
            MainFlowScreen()
        }
    }
}