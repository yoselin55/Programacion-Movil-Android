package com.flores.laboratorio05.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.laboratorio05.model.sampleStudents
import com.flores.laboratorio05.ui.screens.DetailScreen
import com.flores.laboratorio05.ui.screens.HomeScreen
import com.flores.laboratorio05.ui.screens.ListScreen
import com.flores.laboratorio05.ui.screens.LoginScreen
import com.flores.laboratorio05.ui.screens.ProfileScreen

// AppNavigation es el enrutador central: conecta las rutas con sus respectivas pantallas
@Composable
fun AppNavigation() {
    // 1. Crea y recuerda el controlador que gestiona el historial (atrás/adelante)
    val navController = rememberNavController()

    // 2. NavHost es el contenedor donde se van a intercambiar las pantallas.
    // startDestination define con qué pantalla arranca la aplicación (LoginScreen).
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // RUTA 1: Pantalla de inicio de sesión
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // RUTA 2: Pantalla de bienvenida / menú principal
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // RUTA 3: Directorio de alumnos
        composable(route = Screen.List.route) {
            ListScreen(navController = navController)
        }

        // RUTA 4: Expediente académico del alumno seleccionado
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Extrae el identificador del alumno enviado desde ListScreen
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""

            // Busca al alumno correspondiente dentro de la lista de datos de prueba
            val student = sampleStudents.find { it.id == studentId } ?: sampleStudents.first()

            DetailScreen(navController = navController, student = student)
        }

        // RUTA 5: Perfil del usuario
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
