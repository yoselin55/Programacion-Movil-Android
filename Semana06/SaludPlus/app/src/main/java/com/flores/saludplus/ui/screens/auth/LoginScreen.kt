package com.flores.saludplus.ui.screens.auth

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.LOGIN
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: iniciarSesion

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun LoginScreen(onLoginExitoso: () -> Unit, onIrRegistro: () -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Iniciar sesión",
        acciones = listOf("Ingresar" to onLoginExitoso, "Crear cuenta" to onIrRegistro, "Volver" to onBack)
    )
}
