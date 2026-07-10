package com.kaushalprajapati.ecoscan.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Scanner : Destination("scanner")
    data object Result : Destination("result")
    data object NotFound : Destination("not_found")
    data object History : Destination("history")
    data object Profile : Destination("profile")
    data object HelpSupport : Destination("help_support")
    data object About : Destination("about")
}