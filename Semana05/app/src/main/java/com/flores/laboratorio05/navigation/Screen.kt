package com.flores.laboratorio05.navigation

// Clase sellada que actúa como contrato central de navegación
sealed class Screen(val route: String) {
    // Pantalla de inicio
    object Home : Screen(route = "home")

    // Pantalla que muestra la lista de elementos
    object List : Screen(route = "list")

    // Pantalla del perfil del usuario
    object Profile : Screen(route = "profile")

    // Ruta con argumento para el detalle
    object Detail : Screen(route = "detail/{itemId}") {
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}