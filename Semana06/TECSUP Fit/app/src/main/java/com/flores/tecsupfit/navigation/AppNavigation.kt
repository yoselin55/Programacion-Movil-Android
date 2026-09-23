package com.example.semana05_navegacion.navigation

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.semana05_navegacion.screens.*

sealed class BottomItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomItem(Screen.Home.route, "Inicio", Icons.Default.Home)
    object Reservations : BottomItem(Screen.Reservations.route, "Reservas", Icons.Default.List)
    object Routines : BottomItem(Screen.Routines.route, "Rutinas", Icons.Default.DateRange)
    object Profile : BottomItem(Screen.Profile.route, "Perfil", Icons.Default.Person)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onClassClick = { selected ->
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

                DetailScreen(
                    className = name,
                    schedule = schedule,
                    room = room,
                    onBackClick = { navController.popBackStack() },
                    onReserveClick = {
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

            composable(Screen.Reservations.route) { ListScreen() }
            composable(Screen.Routines.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sección de Rutinas")
                }
            }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}