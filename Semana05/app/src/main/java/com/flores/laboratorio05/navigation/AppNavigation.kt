package com.flores.laboratorio05.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.laboratorio05.screens.DetailScreen
import com.flores.laboratorio05.screens.HomeScreen
import com.flores.laboratorio05.screens.ListScreen
import com.flores.laboratorio05.screens.ProfileScreen

// AppNavigation es el enrutador central: conecta las rutas con sus respectivas pantallas
@Composable
fun AppNavigation() {
    // 1. Crea y recuerda el controlador que gestiona el historial (atrás/adelante)
    val navController = rememberNavController()

    // 2. NavHost es el contenedor donde se van a intercambiar las pantallas.
    // startDestination define con qué pantalla arranca la aplicación (HomeScreen).
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // RUTA 1: Si la ruta activa es "home", se invoca y muestra HomeScreen
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // RUTA 2: Si la ruta activa es "list", se invoca y muestra ListScreen
        composable(route = Screen.List.route) {
            ListScreen(navController = navController)
        }

        // RUTA 3: Si la ruta activa es "detail/{itemId}", se configura el argumento
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType } // Define que el argumento debe ser un Entero
            )
        ) { backStackEntry ->
            // Extrae el valor de "itemId" enviado desde ListScreen
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0

            // Invoca DetailScreen pasándole el ID capturado
            DetailScreen(navController = navController, itemId = itemId)
        }

        // RUTA 4: Si la ruta activa es "profile", se invoca y muestra ProfileScreen
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}