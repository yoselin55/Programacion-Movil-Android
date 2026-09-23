package com.flores.tecsupfit.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.flores.tecsupfit.model.User
import com.flores.tecsupfit.screens.*
import kotlinx.coroutines.launch

sealed class BottomItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomItem(Screen.Home.route, "Inicio", Icons.Default.Home)
    object Reservations : BottomItem(Screen.Reservations.route, "Reservas", Icons.Default.List)
    object Routines : BottomItem(Screen.Routines.route, "Rutinas", Icons.Default.DateRange)
    object Profile : BottomItem(Screen.Profile.route, "Perfil", Icons.Default.Person)
}

private const val TOTAL_SLOTS = 12
private const val INITIAL_SLOTS = 8

// Clave para guardar los cupos de cada horario de una clase
private fun slotKey(className: String, time: String) = "$className|$time"

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
    // Cupos disponibles por horario (clave: slotKey(clase, horario))
    val availableSlots = remember { mutableStateMapOf<String, Int>() }

    // Estado elevado: usuarios registrados en memoria (con un usuario de prueba)
    val users = remember {
        mutableStateListOf(User("Diego Ramos", "diego@tecsup.edu.pe", "Tecsup123"))
    }
    // Usuario con sesión iniciada (null = nadie ha iniciado sesión)
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Estado elevado: rutinas y si están completadas
    val routines = remember { mutableStateListOf(*sampleRoutines.toTypedArray()) }

    // Snackbar compartido por todas las pantallas
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss() // oculta el anterior si sigue visible
            snackbarHostState.showSnackbar(message)
        }
    }

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
        snackbarHost = {
            // Sin bottomBar se deja el espacio de la barra de navegación del sistema
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = if (showBottomBar) Modifier else Modifier.navigationBarsPadding()
            )
        },
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
        },
        // Sin insets aquí: cada pantalla tiene su propio Scaffold con topBar que maneja
        // la barra de estado; así no se suma un espacio doble arriba
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            // paddingValues solo trae la altura del bottomBar; al consumirlo, los Scaffold
            // internos no vuelven a sumar el espacio de la barra de navegación del sistema
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        // Si coincide con un usuario registrado se usa ese; si no (o si los campos
                        // están vacíos) se entra igual como usuario de prueba
                        val user = users.find {
                            it.email.equals(email, ignoreCase = true) && it.password == password
                        } ?: User(
                            fullName = email.substringBefore("@").ifBlank { "Invitado" },
                            email = email.ifBlank { "invitado@tecsup.edu.pe" },
                            password = password
                        )
                        currentUser = user
                        // Se elimina Login del back stack para que "atrás" no regrese aquí
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        true
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

                // Horarios y día de la clase (si no está en el catálogo, se usa el horario recibido)
                val gymClass = gymClasses.find { it.name == name }
                val classSchedules = gymClass?.schedules ?: listOf(ClassSchedule(schedule, INITIAL_SLOTS))
                val day = gymClass?.day ?: "Hoy"

                // Cupos y estado de cada horario; los cupos se guardan por clase + horario
                val scheduleOptions = classSchedules.map { s ->
                    ScheduleOption(
                        time = s.time,
                        availableSlots = availableSlots[slotKey(name, s.time)] ?: s.initialSlots,
                        // No se permite reservar dos veces la misma clase en el mismo horario
                        alreadyReserved = reservations.any {
                            it.name == name && it.date == "$day, ${s.time}" && it.status == "Confirmada"
                        }
                    )
                }

                DetailScreen(
                    className = name,
                    room = room,
                    day = day,
                    schedules = scheduleOptions,
                    totalSlots = TOTAL_SLOTS,
                    onBackClick = { navController.popBackStack() },
                    onReserveClick = { selectedTime ->
                        val slots = scheduleOptions.first { it.time == selectedTime }.availableSlots
                        val date = "$day, $selectedTime"
                        reservations.add(0, ReservationItem(name, date, "Confirmada"))
                        availableSlots[slotKey(name, selectedTime)] = slots - 1
                        showMessage("Reservaste $name · $date")
                        // Se envía el día y horario elegido a la pantalla de confirmación
                        navController.navigate(Screen.Confirmation.createRoute(name, date, room))
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

            composable(Screen.Reservations.route) {
                ListScreen(
                    reservations = reservations,
                    onCancelReservation = { item ->
                        val index = reservations.indexOf(item)
                        if (index != -1) {
                            reservations[index] = item.copy(status = "Cancelada")
                            // Se devuelve el cupo al horario de la clase
                            val initial = gymClasses.find { it.name == item.name }
                                ?.schedules?.find { it.time == item.time }?.initialSlots ?: INITIAL_SLOTS
                            val key = slotKey(item.name, item.time)
                            availableSlots[key] = ((availableSlots[key] ?: initial) + 1).coerceAtMost(TOTAL_SLOTS)
                            showMessage("Reserva de ${item.name} cancelada. Cupo liberado")
                        }
                    },
                    onExploreClick = {
                        // Mismo comportamiento que tocar "Inicio" en el bottomBar
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Routines.route) {
                RoutinesScreen(
                    routines = routines,
                    onToggleRoutine = { index ->
                        routines[index] = routines[index].copy(completed = !routines[index].completed)
                    }
                )
            }
            composable(Screen.Profile.route) {
                val user = currentUser ?: return@composable
                ProfileScreen(
                    user = user,
                    // Estadísticas calculadas a partir del estado real
                    reservedCount = reservations.count { it.status == "Confirmada" },
                    completedCount = reservations.count { it.status == "Completada" },
                    routinesCompletedCount = routines.count { it.completed },
                    onSaveName = { newName ->
                        val updated = user.copy(fullName = newName)
                        // Se actualiza también en la lista de usuarios registrados
                        val index = users.indexOfFirst { it.email.equals(user.email, ignoreCase = true) }
                        if (index != -1) users[index] = updated
                        currentUser = updated
                        showMessage("Perfil actualizado")
                    },
                    onLogout = {
                        // Vuelve al Login limpiando todo el back stack
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                        currentUser = null
                    }
                )
            }
        }
    }
}