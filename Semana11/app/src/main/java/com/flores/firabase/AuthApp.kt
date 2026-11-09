package com.flores.firabase

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthApp() {
    val nav = rememberNavController()
    // Si ya hay sesión iniciada, se entra directo a Home
    val inicio = if (FirebaseAuth.getInstance().currentUser != null) "home" else "inicio"

    NavHost(navController = nav, startDestination = inicio) {
        // Pantalla de inicio con foto y los botones Iniciar sesión / Crear cuenta
        composable("inicio") {
            InicioScreen(
                onIniciarSesion = { nav.navigate("login") },
                onCrearCuenta = { nav.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginOk = {
                    nav.navigate("bienvenida") { popUpTo("inicio") { inclusive = true } }
                },
                onIrRegistro = { nav.navigate("register") },
                onVolver = { nav.popBackStack() }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegistroOk = {
                    nav.navigate("bienvenida") { popUpTo("inicio") { inclusive = true } }
                },
                onVolver = { nav.popBackStack() }
            )
        }
        // Mensaje de bienvenida después de iniciar sesión o registrarse
        composable("bienvenida") {
            BienvenidaScreen(
                onContinuar = {
                    nav.navigate("home") { popUpTo("bienvenida") { inclusive = true } }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    nav.navigate("inicio") { popUpTo("home") { inclusive = true } }
                }
            )
        }
    }
}
