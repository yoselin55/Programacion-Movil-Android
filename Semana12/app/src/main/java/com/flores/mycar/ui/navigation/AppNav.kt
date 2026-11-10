package com.flores.mycar.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.AppViewModel
import com.flores.mycar.ui.AvisosViewModel
import com.flores.mycar.ui.MenuViewModel
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.admin.AdminScreen
import com.flores.mycar.ui.admin.AdminViewModel
import com.flores.mycar.ui.admin.GestionScreen
import com.flores.mycar.ui.admin.GestionViewModel
import com.flores.mycar.ui.admin.CatalogoAdminScreen
import com.flores.mycar.ui.admin.CatalogoAdminViewModel
import com.flores.mycar.ui.admin.PromocionesAdminScreen
import com.flores.mycar.ui.admin.PromocionesAdminViewModel
import com.flores.mycar.ui.admin.UsuariosScreen
import com.flores.mycar.ui.admin.UsuariosViewModel
import com.flores.mycar.ui.autos.AgendarCitaScreen
import com.flores.mycar.ui.autos.AgendarCitaViewModel
import com.flores.mycar.ui.autos.AutoDetalleScreen
import com.flores.mycar.ui.autos.AutoDetalleViewModel
import com.flores.mycar.ui.autos.AutosScreen
import com.flores.mycar.ui.autos.AutosViewModel
import com.flores.mycar.ui.autos.PedidosScreen
import com.flores.mycar.ui.autos.PedidosViewModel
import com.flores.mycar.ui.carrito.CarritoScreen
import com.flores.mycar.ui.carrito.CarritoViewModel
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ContenidoMenu
import com.flores.mycar.ui.components.RutasMenu
import com.flores.mycar.ui.detalle.DetalleScreen
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.favoritos.FavoritosScreen
import com.flores.mycar.ui.favoritos.FavoritosViewModel
import com.flores.mycar.ui.inicio.InicioScreen
import com.flores.mycar.ui.lista.ListaScreen
import com.flores.mycar.ui.lista.ListaViewModel
import com.flores.mycar.ui.login.LoginScreen
import com.flores.mycar.ui.login.LoginViewModel
import com.flores.mycar.ui.perfil.PerfilScreen
import com.flores.mycar.ui.perfil.PerfilViewModel
import com.flores.mycar.ui.perfil.TemaViewModel
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import com.flores.mycar.ui.components.AccionesBarra
import com.flores.mycar.ui.components.LocalAccionesBarra
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Storefront
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.launch

private const val RUTA_INICIO = "inicio"
private const val RUTA_AUTOS = "autos"
private const val RUTA_LISTA = "lista"
private const val RUTA_FAVORITOS = "favoritos"
private const val RUTA_CARRITO = "carrito"
private const val RUTA_ADMIN = "admin"
private const val RUTA_PERFIL = "perfil"
private const val RUTA_DETALLE = "detalle/{id}"
private const val RUTA_AUTO_DETALLE = "auto/{autoId}"
private const val RUTA_PEDIDOS = "pedidos"
private const val RUTA_CITA = "cita/{autoId}"
private const val RUTA_CATALOGO_ADMIN = "admin/catalogo"
private const val RUTA_USUARIOS = "admin/usuarios"
private const val RUTA_PROMOCIONES = "admin/promociones"
private const val RUTA_GESTION = "admin/gestion"

/** Pantallas principales: son las que se abren desde el menu lateral. */
private val RUTAS_PRINCIPALES = setOf(
    RUTA_INICIO, RUTA_AUTOS, RUTA_LISTA, RUTA_FAVORITOS, RUTA_CARRITO, RUTA_ADMIN, RUTA_PERFIL,
    RUTA_GESTION, RUTA_CATALOGO_ADMIN, RUTA_PROMOCIONES   // pantallas del administrador (tienen barra inferior)
)

/**
 * Raiz de la navegacion. Segun la sesion muestra: Cargando (revisando si ya habia sesion),
 * el Login (no hay sesion) o la app (sesion iniciada).
 */
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val appVm: AppViewModel = viewModel(factory = factory)
    val estado by appVm.sesion.collectAsStateWithLifecycle()
    val bienvenida by appVm.bienvenida.collectAsStateWithLifecycle()

    when (val e = estado) {
        is UiState.Cargando, is UiState.Error -> Cargando()
        is UiState.Exito -> {
            val sesion = e.datos
            if (sesion == null) {
                val loginVm: LoginViewModel = viewModel(factory = factory)
                LoginScreen(viewModel = loginVm, onSesionIniciada = appVm::sesionIniciada)
            } else {
                NavegacionPrincipal(
                    factory = factory,
                    sesion = sesion,
                    bienvenida = bienvenida,
                    onBienvenidaMostrada = appVm::bienvenidaMostrada,
                    onCerrarSesion = appVm::cerrarSesion
                )
            }
        }
    }
}

/**
 * Menu lateral con las secciones de la app: Inicio, Autos, Marcas, Favoritos, Carrito, Perfil
 * (y Panel Admin solo para el rol admin). El rol decide que pantallas existen.
 */
@Composable
private fun NavegacionPrincipal(
    factory: ViewModelProvider.Factory,
    sesion: Sesion,
    bienvenida: String?,
    onBienvenidaMostrada: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()
    val esAdmin = sesion.rol == Rol.ADMIN
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val alcance = rememberCoroutineScope()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Avisos por notificacion cuando cambia un pedido o una cita.
    val avisos: AvisosViewModel = viewModel(factory = factory)
    LaunchedEffect(sesion.uid) { avisos.iniciar(sesion) }

    // Datos del menu: cantidad del carrito y foto de perfil.
    val menuVm: MenuViewModel = viewModel(factory = factory)
    LaunchedEffect(sesion.uid) { menuVm.usuario(sesion.uid) }
    val carrito by menuVm.carrito.collectAsStateWithLifecycle()
    val foto by menuVm.foto.collectAsStateWithLifecycle()

    val abrirMenu: () -> Unit = { alcance.launch { drawer.open() } }

    // Carrito y campana de pedidos de la barra superior (la campana se actualiza en tiempo real).
    val sinVer by avisos.sinVer.collectAsStateWithLifecycle()
    val accionesBarra = AccionesBarra(
        avisos = sinVer,
        onAvisos = {
            avisos.marcarVistos()
            if (esAdmin) irAPrincipal(nav, RUTA_GESTION) else nav.navigate(RUTA_PEDIDOS)
        }
    )
    val irA: (String) -> Unit = { ruta -> alcance.launch { drawer.close() }; irAPrincipal(nav, ruta) }

    CompositionLocalProvider(LocalAccionesBarra provides accionesBarra) {
    ModalNavigationDrawer(
        drawerState = drawer,
        gesturesEnabled = rutaActual in RUTAS_PRINCIPALES,
        drawerContent = {
            ModalDrawerSheet {
                ContenidoMenu(
                    sesion = sesion,
                    nombre = sesion.nombre,
                    foto = foto,
                    rutaActual = rutaActual,
                    carrito = carrito,
                    rutas = RutasMenu(
                        inicio = RUTA_INICIO,
                        autos = RUTA_AUTOS,
                        marcas = RUTA_LISTA,
                        favoritos = RUTA_FAVORITOS,
                        carrito = RUTA_CARRITO,
                        pedidos = RUTA_PEDIDOS,
                        admin = RUTA_ADMIN,
                        perfil = RUTA_PERFIL,
                        usuarios = RUTA_USUARIOS
                    ),
                    onIr = { ruta ->
                        if (ruta == RUTA_PEDIDOS || ruta == RUTA_USUARIOS) {
                            alcance.launch { drawer.close() }
                            nav.navigate(ruta)
                        } else {
                            irA(ruta)
                        }
                    },
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (rutaActual in RUTAS_PRINCIPALES) {
                    BarraSecciones(esAdmin = esAdmin, rutaActual = rutaActual, onIr = { irAPrincipal(nav, it) })
                }
            }
        ) { relleno ->
        NavHost(
            navController = nav,
            startDestination = if (esAdmin) RUTA_ADMIN else RUTA_INICIO,   // el administrador empieza en su panel
            modifier = Modifier.padding(relleno).consumeWindowInsets(relleno)
        ) {
            composable(RUTA_INICIO) {
                val vm: AutosViewModel = viewModel(factory = factory)
                InicioScreen(
                    viewModel = vm,
                    sesion = sesion,
                    onMenu = abrirMenu,
                    onAutos = { irAPrincipal(nav, RUTA_AUTOS) },
                    onMarcas = { irAPrincipal(nav, RUTA_LISTA) },
                    onFavoritos = { irAPrincipal(nav, RUTA_FAVORITOS) },
                    onCarrito = { irAPrincipal(nav, RUTA_CARRITO) },
                    onAutoClick = { id -> nav.navigate("auto/$id") }
                )
            }
            composable(RUTA_AUTOS) {
                val vm: AutosViewModel = viewModel(factory = factory)
                AutosScreen(
                    viewModel = vm,
                    sesion = sesion,
                    onMenu = abrirMenu,
                    onAutoClick = { id -> nav.navigate("auto/$id") }
                )
            }
            composable(RUTA_LISTA) {
                val vm: ListaViewModel = viewModel(factory = factory)
                ListaScreen(
                    viewModel = vm,
                    onItemClick = { id -> nav.navigate("detalle/$id") },
                    bienvenida = bienvenida,
                    onBienvenidaMostrada = onBienvenidaMostrada,
                    onMenu = abrirMenu
                )
            }
            composable(RUTA_FAVORITOS) {
                val autosVm: AutosViewModel = viewModel(factory = factory)
                val marcasVm: FavoritosViewModel = viewModel(factory = factory)
                FavoritosScreen(
                    autosVm = autosVm,
                    marcasVm = marcasVm,
                    sesion = sesion,
                    onMenu = abrirMenu,
                    onAutoClick = { id -> nav.navigate("auto/$id") },
                    onMarcaClick = { id -> nav.navigate("detalle/$id") }
                )
            }
            composable(RUTA_CARRITO) {
                val vm: CarritoViewModel = viewModel(factory = factory)
                CarritoScreen(
                    viewModel = vm,
                    sesion = sesion,
                    onMenu = abrirMenu,
                    onVerAutos = { irAPrincipal(nav, RUTA_AUTOS) },
                    onAutoClick = { id -> nav.navigate("auto/$id") },
                    onComprado = { nav.navigate(RUTA_PEDIDOS) }
                )
            }
            composable(RUTA_PERFIL) {
                val vm: PerfilViewModel = viewModel(factory = factory)
                val temaVm: TemaViewModel = viewModel(factory = factory)
                PerfilScreen(
                    viewModel = vm,
                    temaViewModel = temaVm,
                    sesion = sesion,
                    onMenu = abrirMenu,
                    onMisPedidos = { nav.navigate(RUTA_PEDIDOS) },
                    onCerrarSesion = onCerrarSesion
                )
            }
            composable(
                route = RUTA_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val vm: DetalleViewModel = viewModel(factory = factory)
                DetalleScreen(
                    viewModel = vm,
                    onVolver = { nav.popBackStack() },
                    onVerAuto = { autoId -> nav.navigate("auto/$autoId") }
                )
            }
            composable(
                route = RUTA_AUTO_DETALLE,
                arguments = listOf(navArgument("autoId") { type = NavType.StringType })
            ) {
                val vm: AutoDetalleViewModel = viewModel(factory = factory)
                AutoDetalleScreen(
                    viewModel = vm,
                    sesion = sesion,
                    onVolver = { nav.popBackStack() },
                    onIrCarrito = { irAPrincipal(nav, RUTA_CARRITO) },
                    onAgendarCita = { autoId -> nav.navigate("cita/$autoId") },
                    onComprado = { nav.navigate(RUTA_PEDIDOS) }
                )
            }
            composable(
                route = RUTA_CITA,
                arguments = listOf(navArgument("autoId") { type = NavType.StringType })
            ) {
                val vm: AgendarCitaViewModel = viewModel(factory = factory)
                AgendarCitaScreen(
                    viewModel = vm,
                    sesion = sesion,
                    onVolver = { nav.popBackStack() },
                    onVerMisCitas = { nav.navigate(RUTA_PEDIDOS) }
                )
            }
            composable(RUTA_PEDIDOS) {
                val vm: PedidosViewModel = viewModel(factory = factory)
                PedidosScreen(viewModel = vm, uid = sesion.uid, onVolver = { nav.popBackStack() })
            }
            if (esAdmin) {
                composable(RUTA_ADMIN) {
                    val vm: AdminViewModel = viewModel(factory = factory)
                    AdminScreen(
                        viewModel = vm,
                        correo = sesion.correo,
                        onGestion = { irAPrincipal(nav, RUTA_GESTION) },
                        onCatalogo = { irAPrincipal(nav, RUTA_CATALOGO_ADMIN) },
                        onUsuarios = { nav.navigate(RUTA_USUARIOS) },
                        onPromociones = { irAPrincipal(nav, RUTA_PROMOCIONES) },
                        onMenu = abrirMenu
                    )
                }
                composable(RUTA_CATALOGO_ADMIN) {
                    val vm: CatalogoAdminViewModel = viewModel(factory = factory)
                    CatalogoAdminScreen(viewModel = vm, onVolver = { irAPrincipal(nav, RUTA_ADMIN) })
                }
                composable(RUTA_PROMOCIONES) {
                    val vm: PromocionesAdminViewModel = viewModel(factory = factory)
                    PromocionesAdminScreen(viewModel = vm, onVolver = { irAPrincipal(nav, RUTA_ADMIN) })
                }
                composable(RUTA_USUARIOS) {
                    val vm: UsuariosViewModel = viewModel(factory = factory)
                    UsuariosScreen(viewModel = vm, onVolver = { nav.popBackStack() })
                }
                composable(RUTA_GESTION) {
                    val vm: GestionViewModel = viewModel(factory = factory)
                    GestionScreen(viewModel = vm, onVolver = { irAPrincipal(nav, RUTA_ADMIN) })
                }
            }
        }
        }
    }
    }
}

/** Abre una pantalla principal sin apilar copias: el atras siempre vuelve a Inicio. */
private fun irAPrincipal(nav: NavHostController, ruta: String) {
    nav.navigate(ruta) {
        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Barra de secciones de abajo (ademas del menu lateral): las pantallas que mas se usan. */
@Composable
private fun BarraSecciones(esAdmin: Boolean, rutaActual: String?, onIr: (String) -> Unit) {
    val secciones = if (esAdmin) listOf(
        Triple(RUTA_ADMIN, "Panel", Icons.Filled.AdminPanelSettings),
        Triple(RUTA_GESTION, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
        Triple(RUTA_CATALOGO_ADMIN, "Catálogo", Icons.Filled.Storefront),
        Triple(RUTA_PROMOCIONES, "Promos", Icons.Filled.LocalOffer),
        Triple(RUTA_PERFIL, "Perfil", Icons.Filled.Person)
    ) else listOf(
        Triple(RUTA_INICIO, "Inicio", Icons.Filled.Home),
        Triple(RUTA_AUTOS, "Autos", Icons.Filled.DirectionsCar),
        Triple(RUTA_PERFIL, "Perfil", Icons.Filled.Person)
    )
    NavigationBar {
        secciones.forEach { (ruta, titulo, icono) ->
            NavigationBarItem(
                selected = rutaActual == ruta,
                onClick = { onIr(ruta) },
                icon = { Icon(icono, contentDescription = titulo) },
                label = { Text(titulo) }
            )
        }
    }
}
