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
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.flores.saludplus.data.repository.Repositorio
import androidx.navigation.compose.currentBackStackEntryAsState
import com.flores.saludplus.ui.components.MenuLateral
import com.flores.saludplus.ui.screens.doctores.DoctoresEspecialidadScreen
import com.flores.saludplus.ui.screens.doctores.DoctoresScreen
import com.flores.saludplus.ui.screens.perfil.PerfilScreen
import com.flores.saludplus.ui.screens.resultados.ResultadosScreen
import com.flores.saludplus.ui.screens.sedes.SedeDetalleScreen
import com.flores.saludplus.ui.screens.sedes.SedesScreen
import kotlinx.coroutines.launch

// Relaciones:
// - Lo llama MainActivity
// - Usa Rutas (navigation/Rutas.kt) para registrar y abrir cada destino
// - Llama a las pantallas de ui/screens y les pasa los parámetros y callbacks
// - Fase 3: envuelve el NavHost en un único ModalNavigationDrawer (MenuLateral) que abren Inicio, Sedes,
//   Doctores y Agenda con onMenu

// Grafo de navegación de toda la app
@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val rutaActual = nav.currentBackStackEntryAsState().value?.destination?.route

    // Navegación de la barra inferior y del menú: vuelve a Inicio y abre el destino sin apilar copias
    val irA: (String) -> Unit = { ruta ->
        nav.navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }
    val abrirMenu: () -> Unit = { scope.launch { drawerState.open() } }

    // Fase 3: sin sesión (por ejemplo, tras reiniciarse la app) no se entra a pantallas protegidas: vuelve al Splash
    LaunchedEffect(rutaActual) {
        if (rutaActual != null && rutaActual !in rutasPublicas && Repositorio.usuarioActual == null) {
            nav.navigate(Rutas.SPLASH) { popUpTo(0) }
        }
    }

    // El botón Atrás del sistema cierra el menú si está abierto
    BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Solo se abre deslizando en las pantallas principales (no en Splash, Registro ni Login)
        gesturesEnabled = rutaActual in rutasConMenu,
        drawerContent = {
            MenuLateral(
                rutaActual = rutaActual,
                onNavegar = { ruta -> scope.launch { drawerState.close(); irA(ruta) } },
                onCerrarSesion = {
                    scope.launch {
                        drawerState.close()
                        // Limpia toda la pila y vuelve al Splash
                        nav.navigate(Rutas.SPLASH) { popUpTo(0) }
                    }
                }
            )
        }
    ) {
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
                onEspecialidad = { id -> nav.navigate(Rutas.doctoresEspecialidad(id)) },
                onVerEspecialidades = { irA(Rutas.DOCTORES) },
                onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) },
                onNavegar = irA,
                onMenu = abrirMenu
            )
        }
        composable(Rutas.SEDES) {
            SedesScreen(
                onSede = { id -> nav.navigate(Rutas.sedeDetalle(id)) },
                onMenu = abrirMenu,
                onNavegar = irA
            )
        }
        composable(
            Rutas.SEDE_DETALLE,
            arguments = listOf(navArgument("sedeId") { type = NavType.IntType })
        ) { entrada ->
            val sedeId = entrada.arguments!!.getInt("sedeId")
            SedeDetalleScreen(
                sedeId = sedeId,
                onAgendar = { nav.navigate(Rutas.especialidades(sedeId)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(Rutas.DOCTORES) {
            DoctoresScreen(
                onEspecialidad = { id -> nav.navigate(Rutas.doctoresEspecialidad(id)) },
                onMenu = abrirMenu,
                onNavegar = irA
            )
        }
        composable(
            Rutas.DOCTORES_ESPECIALIDAD,
            arguments = listOf(navArgument("especialidadId") { type = NavType.IntType })
        ) { entrada ->
            DoctoresEspecialidadScreen(
                especialidadId = entrada.arguments!!.getInt("especialidadId"),
                onAgendar = { medicoId -> nav.navigate(Rutas.fechaHora(medicoId)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.ESPECIALIDADES,
            arguments = listOf(navArgument("sedeId") { type = NavType.IntType })
        ) { entrada ->
            val sedeId = entrada.arguments!!.getInt("sedeId")
            EspecialidadesScreen(
                sedeId = sedeId,
                onEspecialidad = { id -> nav.navigate(Rutas.medicos(sedeId, id)) },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            Rutas.MEDICOS,
            arguments = listOf(
                navArgument("sedeId") { type = NavType.IntType },
                navArgument("especialidadId") { type = NavType.IntType }
            )
        ) { entrada ->
            MedicosScreen(
                sedeId = entrada.arguments!!.getInt("sedeId"),
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
                    // Commit 8: popUpTo(HOME) borra el flujo de agendamiento del historial
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
                onNavegar = irA,
                onMenu = abrirMenu
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
}

// Fase 3: pantallas que se pueden ver sin haber iniciado sesión
private val rutasPublicas = listOf(Rutas.SPLASH, Rutas.REGISTRO, Rutas.LOGIN, Rutas.TERMINOS)

// Fase 3: pantallas principales donde se puede abrir el menú lateral deslizando
private val rutasConMenu = listOf(
    Rutas.HOME, Rutas.SEDES, Rutas.DOCTORES, Rutas.MIS_CITAS, Rutas.RESULTADOS, Rutas.PERFIL
)

// Entra a Inicio y quita Splash, Registro y Login de la pila
private fun entrarAInicio(nav: NavHostController) {
    nav.navigate(Rutas.HOME) { popUpTo(Rutas.SPLASH) { inclusive = true } }
}
