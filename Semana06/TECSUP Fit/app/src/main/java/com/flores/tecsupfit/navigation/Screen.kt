package com.flores.tecsupfit.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    // Uri.encode evita problemas con espacios y ":" (ej. "7:00 am") en la ruta
    object Detail : Screen("detail/{className}/{schedule}/{room}") {
        fun createRoute(className: String, schedule: String, room: String) =
            "detail/${Uri.encode(className)}/${Uri.encode(schedule)}/${Uri.encode(room)}"
    }
    // schedule = horario elegido por el usuario en el detalle
    object Confirmation : Screen("confirmation/{className}/{schedule}/{room}") {
        fun createRoute(className: String, schedule: String, room: String) =
            "confirmation/${Uri.encode(className)}/${Uri.encode(schedule)}/${Uri.encode(room)}"
    }
    object Reservations : Screen("reservations")
    object Routines : Screen("routines")
    object Profile : Screen("profile")
}
