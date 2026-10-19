package com.flores.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.estudiante.LoginScreen
import com.flores.lavacampus.ui.estudiante.SplashScreen
import com.flores.lavacampus.viewmodel.SesionViewModel

private fun destinoDe(rol: Rol) = if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO

// Grafo de navegacion. Splash -> Login (o directo a Inicio/Panel si ya hay perfil)
@Composable
fun NavLava(sesion: SesionViewModel, nav: NavHostController = rememberNavController()) {
    val cargando by sesion.cargando.collectAsState()
    val usuario by sesion.usuarioActual.collectAsState()

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {
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
        // Pantallas provisionales: se reemplazan en los pasos 7 y 11
        composable(Rutas.INICIO) { Provisional("Inicio del estudiante") }
        composable(Rutas.PANEL) { Provisional("Panel del administrador") }
    }
}

@Composable
private fun Provisional(texto: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(texto) }
}
