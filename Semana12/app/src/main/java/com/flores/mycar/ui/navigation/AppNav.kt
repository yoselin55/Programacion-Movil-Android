package com.flores.mycar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flores.mycar.ui.detalle.DetalleScreen
import com.flores.mycar.ui.detalle.DetalleViewModel
import com.flores.mycar.ui.lista.ListaScreen
import com.flores.mycar.ui.lista.ListaViewModel

private const val RUTA_LISTA = "lista"
private const val RUTA_DETALLE = "detalle/{id}"

/** Navegacion de la app: Lista -> Detalle. */
@Composable
fun AppNav(factory: ViewModelProvider.Factory) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate("detalle/$id") }
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
