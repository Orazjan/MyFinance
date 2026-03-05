package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.navigation.routes.AnalizRoute
import com.example.myfinance.navigation.routes.AuthRoute
import com.example.myfinance.navigation.routes.MainRoute
import com.example.myfinance.navigation.routes.ProfileRoute

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), modifier: Modifier
) {

    NavHost(
        navController = navController, startDestination = Graph.Main, modifier = modifier
    ) {
        AuthRoute(navController)
        MainRoute(navController)
        ProfileRoute(navController)
        AnalizRoute(navController)
    }
}