package com.flores.tecsupfit.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.flores.tecsupfit.model.User
import com.flores.tecsupfit.screens.*

sealed class BottomItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomItem(Screen.Home.route, "Inicio", Icons.Default.Home)
    object Reservations : BottomItem(Screen.Reservations.route, "Reservas", Icons.Default.List)
    object Routines : BottomItem(Screen.Routines.route, "Rutinas", Icons.Default.DateRange)
    object Profile : BottomItem(Screen.Profile.route, "Perfil", Icons.Default.Person)
}

private const val TOTAL_SLOTS = 12
private const val INITIAL_SLOTS = 8

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Estado elevado: lista global de reservas compartida entre pantallas
    val reservations = remember {
        mutableStateListOf(
            ReservationItem("Cross Training", "Hoy, 6:00 pm", "Confirmada"),
            ReservationItem("Yoga funcional", "Ayer, 7:00 am", "Completada")
        )
    }
    // Cupos disponibles por clase (clave: nombre de la clase)
    val availableSlots = remember { mutableStateMapOf<String, Int>() }

    // Estado elevado: usuarios registrados en memoria (con un usuario de prueba)
    val users = remember {
        mutableStateListOf(User("Diego Ramos", "diego@tecsup.edu.pe", "Tecsup123"))
    }
    // Usuario con sesión iniciada (null = nadie ha iniciado sesión)
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Si se pierde la sesión (p. ej. al rotar la pantalla) y estamos en una ruta
    // protegida, se regresa al Login para no mostrar pantallas sin usuario
    LaunchedEffect(currentUser, currentRoute) {
        val isAuthRoute = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route
        if (currentUser == null && currentRoute != null && !isAuthRoute) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val bottomItems = listOf(
        BottomItem.Home,
        BottomItem.Reservations,
        BottomItem.Routines,
        BottomItem.Profile
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Reservations.route,
        Screen.Routines.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0F6A52),
                                selectedTextColor = Color(0xFF0F6A52),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        // Busca un usuario con el mismo correo (sin distinguir mayúsculas) y contraseña
                        val user = users.find {
                            it.email.equals(email, ignoreCase = true) && it.password == password
                        }
                        if (user != null) {
                            currentUser = user
                            // Se elimina Login del back stack para que "atrás" no regrese aquí
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        user != null
                    },
                    onRegisterClick = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterClick = { newUser ->
                        // Si el correo ya existía se reemplaza con los nuevos datos
                        users.removeAll { it.email.equals(newUser.email, ignoreCase = true) }
                        users.add(newUser)
                        // Inicia sesión directamente y entra al Home sin volver al Login
                        currentUser = newUser
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                val user = currentUser ?: return@composable
                HomeScreen(userName = user.firstName, onClassClick = { selected ->
                    navController.navigate(
                        Screen.Detail.createRoute(selected.name, selected.schedule, selected.room)
                    )
                })
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("className") { type = NavType.StringType },
                    navArgument("schedule") { type = NavType.StringType },
                    navArgument("room") { type = NavType.StringType }
                )
            ) { backStack ->
                val name = backStack.arguments?.getString("className") ?: ""
                val schedule = backStack.arguments?.getString("schedule") ?: ""
                val room = backStack.arguments?.getString("room") ?: ""

                val slots = availableSlots[name] ?: INITIAL_SLOTS
                val alreadyReserved = reservations.any { it.name == name && it.status == "Confirmada" }

                DetailScreen(
                    className = name,
                    schedule = schedule,
                    room = room,
                    availableSlots = slots,
                    totalSlots = TOTAL_SLOTS,
                    alreadyReserved = alreadyReserved,
                    onBackClick = { navController.popBackStack() },
                    onReserveClick = {
                        reservations.add(0, ReservationItem(name, "Hoy, $schedule", "Confirmada"))
                        availableSlots[name] = slots - 1
                        navController.navigate(Screen.Confirmation.createRoute(name, schedule, room))
                    }
                )
            }

            composable(
                route = Screen.Confirmation.route,
                arguments = listOf(
                    navArgument("className") { type = NavType.StringType },
                    navArgument("schedule") { type = NavType.StringType },
                    navArgument("room") { type = NavType.StringType }
                )
            ) { backStack ->
                val name = backStack.arguments?.getString("className") ?: ""
                val schedule = backStack.arguments?.getString("schedule") ?: ""
                val room = backStack.arguments?.getString("room") ?: ""

                ConfirmationScreen(
                    className = name,
                    schedule = schedule,
                    room = room,
                    onSeeReservationsClick = {
                        navController.navigate(Screen.Reservations.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.Reservations.route) { ListScreen(reservations = reservations) }
            composable(Screen.Routines.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sección de Rutinas")
                }
            }
            composable(Screen.Profile.route) {
                val user = currentUser ?: return@composable
                ProfileScreen(user = user)
            }
        }
    }
}