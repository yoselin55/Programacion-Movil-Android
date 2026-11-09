package com.flores.mycar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.AppViewModel
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.admin.AdminScreen
import com.flores.mycar.ui.admin.AdminViewModel
import com.flores.mycar.ui.components.BarraInferior
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.detalle.DetalleScreen
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.favoritos.FavoritosScreen
import com.flores.mycar.ui.favoritos.FavoritosViewModel
import com.flores.mycar.ui.lista.ListaScreen
import com.flores.mycar.ui.lista.ListaViewModel
import com.flores.mycar.ui.login.LoginScreen
import com.flores.mycar.ui.login.LoginViewModel

private const val RUTA_LISTA = "lista"
private const val RUTA_FAVORITOS = "favoritos"
private const val RUTA_ADMIN = "admin"
private const val RUTA_DETALLE = "detalle/{id}"

/**
 * Raiz de la navegacion. Segun la sesion muestra: Cargando (revisando si ya habia sesion),
 * el Login (no hay sesion) o la app (sesion iniciada).
 */
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val appVm: AppViewModel = viewModel(factory = factory)
    val estado by appVm.sesion.collectAsStateWithLifecycle()

    when (val e = estado) {
        is UiState.Cargando, is UiState.Error -> Cargando()
        is UiState.Exito -> {
            val sesion = e.datos
            if (sesion == null) {
                val loginVm: LoginViewModel = viewModel(factory = factory)
                LoginScreen(viewModel = loginVm, onSesionIniciada = appVm::sesionIniciada)
            } else {
                NavegacionPrincipal(factory, sesion, onCerrarSesion = appVm::cerrarSesion)
            }
        }
    }
}

/**
 * Pestañas Marcas y Favoritos, y el Detalle de una marca.
 * El rol decide que pantallas existen: la ruta Admin solo se registra para el rol admin.
 */
@Composable
private fun NavegacionPrincipal(
    factory: ViewModelProvider.Factory,
    sesion: Sesion,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()
    val esAdmin = sesion.rol == Rol.ADMIN

    NavHost(navController = nav, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate("detalle/$id") },
                onCerrarSesion = onCerrarSesion,
                bottomBar = { Barra(nav, RUTA_LISTA, esAdmin) }
            )
        }
        composable(RUTA_FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate("detalle/$id") },
                bottomBar = { Barra(nav, RUTA_FAVORITOS, esAdmin) }
            )
        }
        if (esAdmin) {
            composable(RUTA_ADMIN) {
                val vm: AdminViewModel = viewModel(factory = factory)
                AdminScreen(
                    viewModel = vm,
                    correo = sesion.correo,
                    bottomBar = { Barra(nav, RUTA_ADMIN, esAdmin) }
                )
            }
        }
        composable(
            route = RUTA_DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val vm: DetalleViewModel = viewModel(factory = factory)
            DetalleScreen(viewModel = vm, onVolver = { nav.popBackStack() })
        }
    }
}

@Composable
private fun Barra(nav: NavHostController, rutaActual: String, esAdmin: Boolean) {
    BarraInferior(
        rutaActual = rutaActual,
        rutaMarcas = RUTA_LISTA,
        rutaFavoritos = RUTA_FAVORITOS,
        rutaAdmin = if (esAdmin) RUTA_ADMIN else null,
        onIr = { ruta ->
            if (ruta != rutaActual) {
                nav.navigate(ruta) {
                    popUpTo(RUTA_LISTA) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}
