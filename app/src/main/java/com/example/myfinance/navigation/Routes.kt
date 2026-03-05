package com.example.myfinance.navigation

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    object AuthGraph

    @Serializable
    object LoginRoute

    @Serializable
    object RegRoute

    @Serializable
    object ResetPasswordRoute
}