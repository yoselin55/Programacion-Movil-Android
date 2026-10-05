package com.flores.saludplus.ui.screens.auth

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.REGISTRO
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: registrarUsuario

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun RegistroScreen(onRegistrado: () -> Unit, onTerminos: () -> Unit, onIrLogin: () -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Registro",
        acciones = listOf("Registrarme" to onRegistrado, "Términos y Condiciones" to onTerminos, "Iniciar sesión" to onIrLogin, "Volver" to onBack)
    )
}
