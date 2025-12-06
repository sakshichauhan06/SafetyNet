package domain

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Incidents: Screen("incidents")
    object Profile: Screen("profile")
}