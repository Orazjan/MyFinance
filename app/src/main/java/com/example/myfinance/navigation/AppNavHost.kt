package com.example.myfinance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myfinance.navigation.graphs.Auth

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController, startDestination = Routes.AuthGraph
    ) {
        Auth(navController)
    }
}