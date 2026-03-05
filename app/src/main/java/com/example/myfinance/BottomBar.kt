package com.example.myfinance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {

        NavigationBarItem(selected = currentRoute == "main", onClick = {
            navController.navigate("main")
        }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })

        NavigationBarItem(selected = currentRoute == "analiz", onClick = {
            navController.navigate("analiz")
        }, icon = { Icon(Icons.Default.Analytics, null) }, label = { Text("Analiz") })

        NavigationBarItem(selected = currentRoute == "profile", onClick = {
            navController.navigate("profile")
        }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
    }
}