package com.flores.laboratorio05.navigation

// Clase sellada que actúa como contrato central de navegación
sealed class Screen(val route: String) {
    // Pantalla de inicio de sesión
    object Login : Screen(route = "login")

    // Pantalla de bienvenida / menú principal
    object Home : Screen(route = "home")

    // Pantalla que muestra el directorio de alumnos
    object List : Screen(route = "list")

    // Pantalla del perfil del usuario
    object Profile : Screen(route = "profile")

    // Ruta con argumento para el expediente académico del alumno
    object Detail : Screen(route = "detail/{studentId}") {
        fun createRoute(studentId: String): String = "detail/$studentId"
    }
}
