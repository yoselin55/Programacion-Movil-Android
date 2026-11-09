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
import com.flores.mycar.ui.AppViewModel
import com.flores.mycar.ui.UiState
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
        is UiState.Exito ->
            if (e.datos == null) {
                val loginVm: LoginViewModel = viewModel(factory = factory)
                LoginScreen(viewModel = loginVm, onSesionIniciada = appVm::sesionIniciada)
            } else {
                NavegacionPrincipal(factory, onCerrarSesion = appVm::cerrarSesion)
            }
    }
}

/** Pestañas Marcas y Favoritos, y el Detalle de una marca. */
@Composable
private fun NavegacionPrincipal(
    factory: ViewModelProvider.Factory,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate("detalle/$id") },
                onCerrarSesion = onCerrarSesion,
                bottomBar = { Barra(nav, RUTA_LISTA) }
            )
        }
        composable(RUTA_FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate("detalle/$id") },
                bottomBar = { Barra(nav, RUTA_FAVORITOS) }
            )
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
private fun Barra(nav: NavHostController, rutaActual: String) {
    BarraInferior(
        rutaActual = rutaActual,
        rutaMarcas = RUTA_LISTA,
        rutaFavoritos = RUTA_FAVORITOS,
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
