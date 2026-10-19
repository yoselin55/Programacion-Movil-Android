package com.flores.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
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
import com.flores.lavacampus.ui.estudiante.DetallePedidoScreen
import com.flores.lavacampus.ui.estudiante.InicioScreen
import com.flores.lavacampus.ui.estudiante.LoginScreen
import com.flores.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.flores.lavacampus.ui.estudiante.SplashScreen
import com.flores.lavacampus.viewmodel.DetallePedidoViewModel
import com.flores.lavacampus.viewmodel.InicioViewModel
import com.flores.lavacampus.viewmodel.LavaViewModelFactory
import com.flores.lavacampus.viewmodel.NuevoPedidoViewModel
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

    Scaffold(
        bottomBar = {
            // La barra solo se ve en las 4 pantallas principales del estudiante
            if (pestanasEstudiante.any { it.ruta == rutaActual }) {
                NavigationBar(containerColor = Color.White) {
                    pestanasEstudiante.forEach { p ->
                        NavigationBarItem(
                            selected = rutaActual == p.ruta,
                            onClick = {
                                if (p.ruta == Rutas.INICIO) {
                                    // Inicio es la base de la pila: se vuelve a ella sin restaurar nada
                                    nav.popBackStack(Rutas.INICIO, inclusive = false)
                                } else {
                                    nav.navigate(p.ruta) {
                                        popUpTo(Rutas.INICIO) { saveState = true }
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
                        onNuevoPedido = { nav.navigate(Rutas.NUEVO_PEDIDO) },
                        onVerPedido = { nav.navigate(Rutas.detalle(it)) },
                        onMisPedidos = { nav.navigate(Rutas.PEDIDOS) },
                        onTarifas = { nav.navigate(Rutas.TARIFAS) }
                    )
                }
            }
            // Pantallas provisionales: se reemplazan en los pasos siguientes
            composable(Rutas.NUEVO_PEDIDO) {
                usuario?.let { u ->
                    val vm: NuevoPedidoViewModel = viewModel(
                        key = "nuevo${u.id}",
                        factory = LavaViewModelFactory(repo, prefs, u)
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
                DetallePedidoScreen(vm = vm, onVolver = { nav.popBackStack() })
            }
            composable(Rutas.PEDIDOS) { Provisional("Mis pedidos (Paso 10)") }
            composable(Rutas.TARIFAS) { Provisional("Tarifas (Paso 10)") }
            composable(Rutas.PERFIL) { Provisional("Perfil (Paso 13)") }
            composable(Rutas.PANEL) { Provisional("Panel del administrador") }
        }
    }
}

@Composable
private fun Provisional(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(texto) }
}
