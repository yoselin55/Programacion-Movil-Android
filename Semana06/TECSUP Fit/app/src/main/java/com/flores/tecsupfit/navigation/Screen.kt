package com.example.semana05_navegacion.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{className}/{schedule}/{room}") {
        fun createRoute(className: String, schedule: String, room: String) =
            "detail/$className/$schedule/$room"
    }
    object Confirmation : Screen("confirmation/{className}/{schedule}/{room}") {
        fun createRoute(className: String, schedule: String, room: String) =
            "confirmation/$className/$schedule/$room"
    }
    object Reservations : Screen("reservations")
    object Routines : Screen("routines")
    object Profile : Screen("profile")
}