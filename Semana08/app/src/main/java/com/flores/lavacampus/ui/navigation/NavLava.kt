package com.flores.lavacampus.ui.navigation

import kotlinx.coroutines.delay
import com.flores.lavacampus.ui.components.LogoLavaCampus
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.background
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
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
import androidx.compose.runtime.remember
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
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DryCleaning
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DryCleaning
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.key
import com.flores.lavacampus.ui.components.BarraFlotante
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.flores.lavacampus.ui.components.ItemMenu
import com.flores.lavacampus.ui.components.MenuLateral
import com.flores.lavacampus.ui.components.MenuLateralVacio
import com.flores.lavacampus.ui.components.ItemBarra
import com.flores.lavacampus.ui.notificaciones.AvisoEmergente
import com.flores.lavacampus.ui.notificaciones.NotificacionesScreen
import com.flores.lavacampus.viewmodel.NotificacionesViewModel
import com.flores.lavacampus.viewmodel.ClienteDetalleViewModel
import com.flores.lavacampus.ui.administrador.ClienteDetalleScreen
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

// Pestanas de la barra inferior: icono de contorno y, cuando esta activa, el mismo icono relleno
private class Pestana(val ruta: String, val texto: String, val icono: ImageVector, val iconoActivo: ImageVector)

private val pestanasEstudiante = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Outlined.Home, Icons.Rounded.Home),
    Pestana(Rutas.PEDIDOS, "Pedidos", Icons.Outlined.LocalLaundryService, Icons.Rounded.LocalLaundryService),
    Pestana(Rutas.PERFIL, "Perfil", Icons.Outlined.Person, Icons.Rounded.Person)
)

private val pestanasAdmin = listOf(
    Pestana(Rutas.PANEL, "Panel", Icons.Outlined.Dashboard, Icons.Rounded.Dashboard),
    Pestana(Rutas.BANDEJA, "Pedidos", Icons.Outlined.Inbox, Icons.Rounded.Inbox),
    Pestana(Rutas.CLIENTES, "Clientes", Icons.Outlined.Groups, Icons.Rounded.Groups)
)

// Pantallas que se abren desde el menu lateral (no estan en la barra de abajo, pero la barra se ve en ellas)
private val rutasDelMenuEstudiante = listOf(Rutas.TARIFAS)
private val rutasDelMenuAdmin = listOf(Rutas.SERVICIOS, Rutas.AJUSTES)

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
    val foto by sesion.foto.collectAsState()
    val rutaActual = nav.currentBackStackEntryAsState().value?.destination?.route

    // Mensaje de bienvenida al iniciar sesion o registrarse: se ve en la pantalla unos segundos
    val bienvenida by sesion.bienvenida.collectAsState()
    val avisosActivados by sesion.notificaciones.collectAsState()
    var textoBienvenida by remember { mutableStateOf("") }
    LaunchedEffect(bienvenida) {
        bienvenida?.let {
            textoBienvenida = it
            delay(5000) // dura 5 segundos
            sesion.limpiarBienvenida()
        }
    }

    // Cambiar de perfil: navega al grafo del otro rol y limpia la pila
    fun irAlPerfil(rol: Rol) {
        nav.navigate(destinoDe(rol)) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    fun irAlLogin() {
        nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
    }

    // Menu lateral: se abre con el boton de tres rayas (o deslizando desde el borde) en las pantallas principales
    val estadoMenu = rememberDrawerState(DrawerValue.Closed)
    val ambito = rememberCoroutineScope()
    val esAdminActual = usuario?.rol == Rol.ADMIN.name
    val pestanasActuales = if (esAdminActual) pestanasAdmin else pestanasEstudiante
    val rutasDelMenu = if (esAdminActual) rutasDelMenuAdmin else rutasDelMenuEstudiante
    val enPrincipal = usuario != null && (pestanasActuales.any { it.ruta == rutaActual } || rutaActual in rutasDelMenu)
    val sinLeerMenu = usuario?.let { u ->
        val vmMenu: NotificacionesViewModel = viewModel(key = "notiMenu${u.id}", factory = LavaViewModelFactory(repo, prefs, u))
        vmMenu.noLeidas.collectAsState().value
    } ?: 0
    val abrirMenu: () -> Unit = { ambito.launch { estadoMenu.open() } }

    ModalNavigationDrawer(
        drawerState = estadoMenu,
        gesturesEnabled = enPrincipal,
        scrimColor = Color.Black.copy(alpha = 0.3f),
        drawerContent = {
            if (usuario == null) MenuLateralVacio()
            usuario?.let { u ->
                val esAdmin = u.rol == Rol.ADMIN.name
                val base = if (esAdmin) Rutas.PANEL else Rutas.INICIO
                // El menu no repite la barra de abajo: aqui van las demas secciones
                val items = if (esAdmin) listOf(
                    ItemMenu("Notificaciones", Icons.Outlined.Notifications, Rutas.NOTIFICACIONES, sinLeerMenu),
                    ItemMenu("Servicios", Icons.Outlined.DryCleaning, Rutas.SERVICIOS),
                    ItemMenu("Ajustes", Icons.Outlined.Settings, Rutas.AJUSTES)
                ) else listOf(
                    ItemMenu("Notificaciones", Icons.Outlined.Notifications, Rutas.NOTIFICACIONES, sinLeerMenu),
                    ItemMenu("Tarifas", Icons.Outlined.Sell, Rutas.TARIFAS),
                    ItemMenu("Nuevo pedido", Icons.Outlined.AddCircleOutline, Rutas.nuevoPedido())
                )
                MenuLateral(
                    nombre = u.nombre,
                    correo = u.correo,
                    etiqueta = if (esAdmin) Rol.ADMIN.texto else Rol.CLIENTE.texto,
                    foto = foto,
                    items = items,
                    rutaActual = rutaActual,
                    onElegir = { ruta ->
                        ambito.launch { estadoMenu.close() }
                        when {
                            ruta == base -> nav.popBackStack(base, inclusive = false)
                            ruta == Rutas.nuevoPedido() -> nav.navigate(ruta)
                            else -> nav.navigate(ruta) {
                                popUpTo(base) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onCerrarSesion = {
                        ambito.launch { estadoMenu.close() }
                        sesion.cerrarSesion(::irAlLogin)
                    }
                )
            }
        }
    ) {
    Scaffold(
        bottomBar = {
            // La barra solo se ve en las pantallas principales de cada perfil
            if (enPrincipal) {
                val pestanas = pestanasActuales
                val base = pestanas.first().ruta // Inicio o Panel: base de la pila
                BarraFlotante(
                    items = pestanas.map {
                        ItemBarra(it.texto, it.icono, it.iconoActivo)
                    },
                    seleccionada = pestanas.indexOfFirst { it.ruta == rutaActual },
                    onElegir = { i ->
                        val p = pestanas[i]
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
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { relleno ->
        Box(Modifier.fillMaxSize()) {
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
                        onTarifas = { nav.navigate(Rutas.TARIFAS) },
                        foto = foto,
                        onMenu = abrirMenu
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
                usuario?.let { u ->
                    PanelScreen(
                        vm = vm,
                        onVerListos = { nav.navigate(Rutas.BANDEJA) },
                        foto = foto,
                        onMenu = abrirMenu
                    )
                }
            }
            composable(Rutas.NOTIFICACIONES) {
                usuario?.let { u ->
                    val vm: NotificacionesViewModel = viewModel(
                        key = "notis${u.id}",
                        factory = LavaViewModelFactory(repo, prefs, u)
                    )
                    NotificacionesScreen(
                        vm = vm,
                        onVolver = { nav.popBackStack() },
                        // El estudiante abre el detalle de su pedido; el administrador, su gestion
                        onAbrirPedido = { id ->
                            nav.navigate(if (u.rol == Rol.ADMIN.name) Rutas.gestion(id) else Rutas.detalle(id))
                        }
                    )
                }
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
                ClientesScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack(Rutas.PANEL, inclusive = false) },
                    onAbrir = { nav.navigate(Rutas.cliente(it)) }
                )
            }
            composable(
                Rutas.CLIENTE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                val vm: ClienteDetalleViewModel = viewModel(
                    key = "cliente$id",
                    factory = LavaViewModelFactory(repo, prefs, clienteId = id)
                )
                ClienteDetalleScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack() },
                    onAbrirPedido = { nav.navigate(Rutas.gestion(it)) }
                )
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

        // Aviso emergente: llega una notificacion nueva mientras se usa la app (tiempo real)
        usuario?.let { u ->
            key(u.id) {
                val vmAviso: NotificacionesViewModel = viewModel(
                    key = "notiAviso${u.id}",
                    factory = LavaViewModelFactory(repo, prefs, u)
                )
                AvisoEmergente(
                    vm = vmAviso,
                    habilitado = avisosActivados && rutaActual != Rutas.NOTIFICACIONES && rutaActual != Rutas.LOGIN,
                    onAbrir = { nav.navigate(Rutas.NOTIFICACIONES) },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }

        // Aviso de bienvenida (en el centro de la pantalla, con el logo)
        AnimatedVisibility(
            visible = bienvenida != null,
            enter = scaleIn(initialScale = 0.8f) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LogoLavaCampus(88.dp)
                Text(
                    textoBienvenida,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        }
    }
    }
}
