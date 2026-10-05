package com.flores.saludplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.saludplus.ui.screens.agendamiento.CitaExitosaScreen
import com.flores.saludplus.ui.screens.agendamiento.ConfirmarCitaScreen
import com.flores.saludplus.ui.screens.agendamiento.EspecialidadesScreen
import com.flores.saludplus.ui.screens.agendamiento.FechaHoraScreen
import com.flores.saludplus.ui.screens.agendamiento.MedicosScreen
import com.flores.saludplus.ui.screens.auth.LoginScreen
import com.flores.saludplus.ui.screens.auth.RegistroScreen
import com.flores.saludplus.ui.screens.auth.SplashScreen
import com.flores.saludplus.ui.screens.auth.TerminosScreen
import com.flores.saludplus.ui.screens.citas.DetalleCitaScreen
import com.flores.saludplus.ui.screens.citas.MisCitasScreen
import com.flores.saludplus.ui.screens.home.HomeScreen
import com.flores.saludplus.ui.screens.notificaciones.NotificacionesScreen
import com.flores.saludplus.ui.screens.perfil.PerfilScreen
import com.flores.saludplus.ui.screens.resultados.ResultadosScreen

// Relaciones:
// - Lo llama MainActivity
// - Usa Rutas (navigation/Rutas.kt) para registrar y abrir cada destino
// - Llama a las 15 pantallas de ui/screens y les pasa los parámetros y callbacks

// Grafo de navegación de toda la app
@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    // Navegación de la barra inferior: vuelve a Inicio y abre el destino sin apilar copias
    val irA: (String) -> Unit = { ruta ->
        nav.navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { nav.navigate(Rutas.REGISTRO) },
                onYaTengoCuenta = { nav.navigate(Rutas.LOGIN) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrado = { entrarAInicio(nav) },
                onTerminos = { nav.navigate(Rutas.TERMINOS) },
                onIrLogin = { nav.navigate(Rutas.LOGIN) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { entrarAInicio(nav) },
                onIrRegistro = { nav.navigate(Rutas.REGISTRO) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(Rutas.TERMINOS) {
            TerminosScreen(onBack = { nav.popBackStack() })
        }
        composable(Rutas.HOME) {
            HomeScreen(
                onAgendar = { nav.navigate(Rutas.ESPECIALIDADES) },
                onEspecialidad = { id -> nav.navigate(Rutas.medicos(id)) },
                onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) },
                onNavegar = irA
            )
        }
        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onEspecialidad = { id -> nav.navigate(Rutas.medicos(id)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.MEDICOS,
            arguments = listOf(navArgument("especialidadId") { type = NavType.IntType })
        ) { entrada ->
            MedicosScreen(
                especialidadId = entrada.arguments!!.getInt("especialidadId"),
                onMedico = { id -> nav.navigate(Rutas.fechaHora(id)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.FECHA_HORA,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { entrada ->
            val medicoId = entrada.arguments!!.getInt("medicoId")
            FechaHoraScreen(
                medicoId = medicoId,
                onContinuar = { fecha, hora -> nav.navigate(Rutas.confirmar(medicoId, fecha, hora)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.CONFIRMAR,
            arguments = listOf(
                navArgument("medicoId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )
        ) { entrada ->
            ConfirmarCitaScreen(
                medicoId = entrada.arguments!!.getInt("medicoId"),
                fecha = entrada.arguments!!.getString("fecha")!!,
                hora = entrada.arguments!!.getString("hora")!!,
                onCitaAgendada = { citaId ->
                    // Borra el flujo de agendamiento del historial hasta Inicio
                    nav.navigate(Rutas.citaExitosa(citaId)) { popUpTo(Rutas.HOME) }
                },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.CITA_EXITOSA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
        ) { entrada ->
            CitaExitosaScreen(
                citaId = entrada.arguments!!.getInt("citaId"),
                onVerMisCitas = { irA(Rutas.MIS_CITAS) },
                onIrInicio = { irA(Rutas.HOME) }
            )
        }
        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(
                onDetalle = { id -> nav.navigate(Rutas.detalleCita(id)) },
                onNavegar = irA
            )
        }
        composable(Rutas.RESULTADOS) {
            ResultadosScreen(onNavegar = irA)
        }
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onNavegar = irA,
                onCerrarSesion = {
                    // Limpia toda la pila y vuelve al Splash
                    nav.navigate(Rutas.SPLASH) { popUpTo(0) }
                }
            )
        }
        composable(
            Rutas.DETALLE_CITA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
        ) { entrada ->
            DetalleCitaScreen(
                citaId = entrada.arguments!!.getInt("citaId"),
                onBack = { nav.popBackStack() }
            )
        }
        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen(onBack = { nav.popBackStack() })
        }
    }
}

// Entra a Inicio y quita Splash, Registro y Login de la pila
private fun entrarAInicio(nav: NavHostController) {
    nav.navigate(Rutas.HOME) { popUpTo(Rutas.SPLASH) { inclusive = true } }
}
