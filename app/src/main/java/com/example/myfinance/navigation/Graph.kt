package com.example.myfinance.navigation

sealed class Graph(val route: String) {
    object Auth : Graph("auth_graph")
    object Main : Graph("main_graph")
}