package com.flores.saludplus.ui.screens.auth

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.TERMINOS
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: ninguna

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun TerminosScreen(onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Términos y condiciones",
        acciones = listOf("Volver" to onBack)
    )
}
