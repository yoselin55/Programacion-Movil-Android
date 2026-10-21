package com.flores.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.administrador.AjustesScreen
import com.flores.lavacampus.ui.administrador.BandejaScreen
import com.flores.lavacampus.ui.administrador.ClientesScreen
import com.flores.lavacampus.ui.administrador.FormularioServicioScreen
import com.flores.lavacampus.ui.administrador.ServiciosScreen
import com.flores.lavacampus.ui.estudiante.PerfilScreen
import com.flores.lavacampus.viewmodel.ClientesViewModel
import com.flores.lavacampus.viewmodel.PerfilViewModel
import com.flores.lavacampus.viewmodel.ServiciosViewModel
import com.flores.lavacampus.ui.administrador.GestionPedidoScreen
import com.flores.lavacampus.ui.administrador.PanelScreen
import com.flores.lavacampus.ui.estudiante.DetallePedidoScreen
import com.flores.lavacampus.ui.estudiante.InicioScreen
import com.flores.lavacampus.ui.estudiante.LoginScreen
import com.flores.lavacampus.ui.estudiante.MisPedidosScreen
import com.flores.lavacampus.ui.estudiante.TarifasScreen
import com.flores.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.flores.lavacampus.ui.estudiante.SplashScreen
import com.flores.lavacampus.viewmodel.BandejaViewModel
import com.flores.lavacampus.viewmodel.DetallePedidoViewModel
import com.flores.lavacampus.viewmodel.GestionPedidoViewModel
import com.flores.lavacampus.viewmodel.InicioViewModel
import com.flores.lavacampus.viewmodel.LavaViewModelFactory
import com.flores.lavacampus.viewmodel.MisPedidosViewModel
import com.flores.lavacampus.viewmodel.TarifasViewModel
import com.flores.lavacampus.viewmodel.NuevoPedidoViewModel
import com.flores.lavacampus.viewmodel.PanelViewModel
import com.flores.lavacampus.viewmodel.SesionViewModel

private fun destinoDe(rol: Rol) = if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO

// Pestanas de la barra inferior del estudiante
private class Pestana(val ruta: String, val texto: String, val icono: ImageVector)

private val pestanasEstudiante = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Outlined.Home),
    Pestana(Rutas.PEDIDOS, "Pedidos", Icons.Outlined.Menu),
    Pestana(Rutas.TARIFAS, "Tarifas", Icons.Outlined.Sell),
    Pestana(Rutas.PERFIL, "Perfil", Icons.Outlined.Person)
)

private val pestanasAdmin = listOf(
    Pestana(Rutas.PANEL, "Panel", Icons.Outlined.BarChart),
    Pestana(Rutas.BANDEJA, "Pedidos", Icons.Outlined.Menu),
    Pestana(Rutas.SERVICIOS, "Servicios", Icons.Outlined.Sell),
    Pestana(Rutas.CLIENTES, "Clientes", Icons.Outlined.Person),
    Pestana(Rutas.AJUSTES, "Ajustes", Icons.Outlined.Settings)
)

// Grafo de navegacion. Splash -> Login (o directo a Inicio/Panel si ya hay perfil)
@Composable
fun NavLava(
    sesion: SesionViewModel,
    repo: LavaRepository,
    prefs: PreferenciasStore,
    nav: NavHostController = rememberNavController()
) {
    val cargando by sesion.cargando.collectAsState()
    val usuario by sesion.usuarioActual.collectAsState()
    val rutaActual = nav.currentBackStackEntryAsState().value?.destination?.route

    // Cambiar de perfil: navega al grafo del otro rol y limpia la pila
    fun irAlPerfil(rol: Rol) {
        nav.navigate(destinoDe(rol)) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    fun irAlLogin() {
        nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    Scaffold(
        bottomBar = {
            // La barra solo se ve en las pantallas principales de cada perfil
            val pestanas = listOf(pestanasEstudiante, pestanasAdmin)
                .firstOrNull { grupo -> grupo.any { it.ruta == rutaActual } }
            if (pestanas != null) {
                val base = pestanas.first().ruta // Inicio o Panel: base de la pila
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    pestanas.forEach { p ->
                        NavigationBarItem(
                            selected = rutaActual == p.ruta,
                            onClick = {
                                if (p.ruta == base) {
                                    // La base de la pila: se vuelve a ella sin restaurar nada
                                    nav.popBackStack(base, inclusive = false)
                                } else {
                                    nav.navigate(p.ruta) {
                                        popUpTo(base) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(p.icono, contentDescription = p.texto) },
                            label = { Text(p.texto) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { relleno ->
        NavHost(
            navController = nav,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(bottom = relleno.calculateBottomPadding())
        ) {
            composable(Rutas.SPLASH) {
                SplashScreen()
                LaunchedEffect(cargando) {
                    if (!cargando) {
                        val destino = usuario?.let { destinoDe(Rol.valueOf(it.rol)) } ?: Rutas.LOGIN
                        // popUpTo limpia el Splash de la pila: atras no vuelve a el
                        nav.navigate(destino) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                    }
                }
            }
            composable(Rutas.LOGIN) {
                LoginScreen(sesion) { rol ->
                    nav.navigate(destinoDe(rol)) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                }
            }
            composable(Rutas.INICIO) {
                usuario?.let { u ->
                    // Un ViewModel por estudiante (key) para que no se mezclen sus pedidos
                    val vm: InicioViewModel = viewModel(
                        key = "inicio${u.id}",
                        factory = LavaViewModelFactory(repo, prefs, u)
                    )
                    InicioScreen(
                        vm = vm,
                        onNuevoPedido = { nav.navigate(Rutas.nuevoPedido()) },
                        onVerPedido = { nav.navigate(Rutas.detalle(it)) },
                        onMisPedidos = { nav.navigate(Rutas.PEDIDOS) },
                        onTarifas = { nav.navigate(Rutas.TARIFAS) }
                    )
                }
            }
            // Pantallas provisionales: se reemplazan en los pasos siguientes
            composable(
                Rutas.NUEVO_PEDIDO,
                arguments = listOf(navArgument("repetir") { type = NavType.IntType; defaultValue = 0 })
            ) { entrada ->
                usuario?.let { u ->
                    // Si viene un id, el ViewModel carga las lineas de ese pedido con los precios actuales
                    val repetir = entrada.arguments?.getInt("repetir") ?: 0
                    val vm: NuevoPedidoViewModel = viewModel(
                        key = "nuevo${u.id}-$repetir",
                        factory = LavaViewModelFactory(repo, prefs, u, repetirId = repetir)
                    )
                    NuevoPedidoScreen(
                        vm = vm,
                        onVolver = { nav.popBackStack() },
                        // Al crear el pedido se vuelve a Mis pedidos (sin dejar Nuevo pedido atras)
                        onPedidoCreado = {
                            nav.navigate(Rutas.PEDIDOS) { popUpTo(Rutas.NUEVO_PEDIDO) { inclusive = true } }
                        }
                    )
                }
            }
            composable(
                Rutas.DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                val vm: DetallePedidoViewModel = viewModel(
                    key = "detalle$id",
                    factory = LavaViewModelFactory(repo, prefs, pedidoId = id)
                )
                DetallePedidoScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack() },
                    onRepetir = { nav.navigate(Rutas.nuevoPedido(it)) }
                )
            }
            composable(Rutas.PEDIDOS) {
                usuario?.let { u ->
                    val vm: MisPedidosViewModel = viewModel(
                        key = "pedidos${u.id}",
                        factory = LavaViewModelFactory(repo, prefs, u)
                    )
                    MisPedidosScreen(
                        vm = vm,
                        onVolver = { nav.popBackStack(Rutas.INICIO, inclusive = false) },
                        onVerPedido = { nav.navigate(Rutas.detalle(it)) },
                        onNuevoPedido = { nav.navigate(Rutas.nuevoPedido()) },
                        onRepetir = { nav.navigate(Rutas.nuevoPedido(it)) }
                    )
                }
            }
            composable(Rutas.TARIFAS) {
                val vm: TarifasViewModel = viewModel(factory = LavaViewModelFactory(repo, prefs))
                TarifasScreen(vm = vm, onVolver = { nav.popBackStack(Rutas.INICIO, inclusive = false) })
            }
            composable(Rutas.PERFIL) {
                usuario?.let { u ->
                    val vm: PerfilViewModel = viewModel(
                        key = "perfil${u.id}",
                        factory = LavaViewModelFactory(repo, prefs, u)
                    )
                    PerfilScreen(
                        vm = vm,
                        sesion = sesion,
                        onVolver = { nav.popBackStack(Rutas.INICIO, inclusive = false) },
                        onCambiarPerfil = ::irAlPerfil,
                        onCerrarSesion = ::irAlLogin
                    )
                }
            }
            composable(Rutas.PANEL) {
                val vm: PanelViewModel = viewModel(factory = LavaViewModelFactory(repo, prefs))
                PanelScreen(vm = vm, onVerListos = { nav.navigate(Rutas.BANDEJA) })
            }
            composable(Rutas.BANDEJA) {
                val vm: BandejaViewModel = viewModel(factory = LavaViewModelFactory(repo, prefs))
                BandejaScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack(Rutas.PANEL, inclusive = false) },
                    onAbrir = { nav.navigate(Rutas.gestion(it)) }
                )
            }
            composable(
                Rutas.GESTION,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                val vm: GestionPedidoViewModel = viewModel(
                    key = "gestion$id",
                    factory = LavaViewModelFactory(repo, prefs, pedidoId = id)
                )
                GestionPedidoScreen(vm = vm, onVolver = { nav.popBackStack() })
            }
            composable(Rutas.SERVICIOS) {
                val vm: ServiciosViewModel = viewModel(factory = LavaViewModelFactory(repo, prefs))
                ServiciosScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack(Rutas.PANEL, inclusive = false) },
                    onEditar = { nav.navigate(Rutas.servicio(it)) },
                    onNuevo = { nav.navigate(Rutas.servicio(0)) }
                )
            }
            composable(
                Rutas.SERVICIO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                val vm: ServiciosViewModel = viewModel(
                    key = "formServicio",
                    factory = LavaViewModelFactory(repo, prefs)
                )
                FormularioServicioScreen(vm = vm, servicioId = id, onVolver = { nav.popBackStack() })
            }
            composable(Rutas.CLIENTES) {
                val vm: ClientesViewModel = viewModel(factory = LavaViewModelFactory(repo, prefs))
                ClientesScreen(vm = vm, onVolver = { nav.popBackStack(Rutas.PANEL, inclusive = false) })
            }
            composable(Rutas.AJUSTES) {
                AjustesScreen(
                    sesion = sesion,
                    onVolver = { nav.popBackStack(Rutas.PANEL, inclusive = false) },
                    onCambiarPerfil = ::irAlPerfil,
                    onCerrarSesion = ::irAlLogin
                )
            }
        }
    }
}

@Composable
private fun Provisional(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(texto) }
}
