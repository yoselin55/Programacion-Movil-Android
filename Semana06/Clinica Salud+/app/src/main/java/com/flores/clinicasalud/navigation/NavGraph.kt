package com.flores.clinicasalud.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.flores.clinicasalud.screens.*
import com.flores.clinicasalud.ui.theme.*
import kotlinx.coroutines.launch

// =========================================================================
// REQUISITO CUMPLIDO: Menú lateral (Drawer) y Navegación

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Lista de citas dinámica para reflejar la nueva cita agendada
    val appointmentsList = remember {
        mutableStateListOf(
            AppointmentItem(
                id = "1",
                doctorName = "Dra. Ana Torres",
                specialty = "Viernes 27, 10:30 am",
                date = "",
                time = "",
                status = "Confirmada"
            ),
            AppointmentItem(
                id = "2",
                doctorName = "Dr. Luis Vega",
                specialty = "Miércoles 15, 3:00 pm",
                date = "",
                time = "",
                status = "Completada"
            )
        )
    }

    var selectedDrawerRoute by remember { mutableStateOf(Screen.Home.route) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Cabecera del Drawer (Idéntica a la Figura 2)
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(LightPurpleCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "JP",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPurple,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Juan Pérez",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Text(
                                text = "Paciente",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Ítems del Drawer (Figura 2)
                    val menuItems = listOf(
                        "Inicio" to Screen.Home.route,
                        "Mis citas" to Screen.MyAppointments.route,
                        "Historial médico" to Screen.MedicalHistory.route,
                        "Perfil" to Screen.Profile.route
                    )

                    menuItems.forEach { (label, route) ->
                        val isSelected = selectedDrawerRoute == route
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) LightPurpleCard else Color.Transparent)
                                .clickable {
                                    selectedDrawerRoute = route
                                    scope.launch { drawerState.close() }
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = if (isSelected) PrimaryPurple else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryPurple else TextDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            // 1. Pantalla de Inicio
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onDoctorClick = { doctorId ->
                        navController.navigate(Screen.DoctorDetail.createRoute(doctorId))
                    }
                )
            }

            // 2. Perfil del médico
            composable(
                route = Screen.DoctorDetail.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: "1"
                DoctorDetailScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.popBackStack() },
                    onAgendarCitaClick = { id ->
                        navController.navigate(Screen.ScheduleAppointment.createRoute(id))
                    }
                )
            }

            // 3. Agendar Cita
            composable(
                route = Screen.ScheduleAppointment.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: "1"
                ScheduleAppointmentScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.popBackStack() },
                    onConfirmClick = { date, time ->
                        val doc = sampleDoctors.find { it.id == doctorId }
                        doc?.let {
                            appointmentsList.add(
                                0,
                                AppointmentItem(
                                    id = System.currentTimeMillis().toString(),
                                    doctorName = it.name,
                                    specialty = "$date, $time am",
                                    date = date,
                                    time = time,
                                    status = "Confirmada"
                                )
                            )
                        }
                        navController.navigate(
                            Screen.Confirmation.createRoute(doctorId, date, time)
                        )
                    }
                )
            }

            // 4. Confirmación
            composable(
                route = Screen.Confirmation.route,
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: "1"
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""

                ConfirmationScreen(
                    doctorId = doctorId,
                    date = date,
                    time = time,
                    onVerMisCitasClick = {
                        selectedDrawerRoute = Screen.MyAppointments.route
                        navController.navigate(Screen.MyAppointments.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // 5. Mis Citas
            composable(Screen.MyAppointments.route) {
                MyAppointmentsScreen(
                    appointments = appointmentsList,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            // 6. Historial Médico
            composable(Screen.MedicalHistory.route) {
                MedicalHistoryScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            // 7. Perfil
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}