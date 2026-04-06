import kotlinx.serialization.Serializable

sealed interface Graph {
    @Serializable
    object Auth : Graph
    @Serializable
    object Main : Graph
}

sealed interface MainDestinations {
    @Serializable
    object Home : MainDestinations
    @Serializable
    object Analytics : MainDestinations
    @Serializable
    object Profile : MainDestinations
}

sealed interface AppDestination {
    @Serializable
    object Settings : AppDestination
    @Serializable
    object Templates : AppDestination
    @Serializable
    object VersionOfApp : AppDestination
    @Serializable
    object AddTransaction : AppDestination
    @Serializable
    object AddTemplate : AppDestination
}

sealed interface AuthDestination {
    @Serializable
    object Login : AuthDestination
    @Serializable
    object Registration : AuthDestination
    @Serializable
    object ResetPassword : AuthDestination
}