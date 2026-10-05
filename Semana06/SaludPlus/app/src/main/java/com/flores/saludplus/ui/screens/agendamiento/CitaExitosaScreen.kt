package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.CITA_EXITOSA
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: obtenerCita, obtenerMedico

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onIrInicio: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Cita agendada ($citaId)",
        acciones = listOf("Ver mis citas" to onVerMisCitas, "Ir al inicio" to onIrInicio)
    )
}
