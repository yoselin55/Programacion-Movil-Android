package com.flores.saludplus.ui.screens.notificaciones

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.NOTIFICACIONES
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: citasDelUsuario

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun NotificacionesScreen(onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Notificaciones",
        acciones = listOf("Volver" to onBack)
    )
}
