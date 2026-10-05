package com.flores.saludplus.ui.screens.citas

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.DETALLE_CITA
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: obtenerCita, cancelarCita

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun DetalleCitaScreen(citaId: Int, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Detalle de cita ($citaId)",
        acciones = listOf("Volver" to onBack)
    )
}
