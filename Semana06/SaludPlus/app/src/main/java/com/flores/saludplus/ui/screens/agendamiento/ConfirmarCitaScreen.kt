package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.CONFIRMAR
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: obtenerMedico, agendarCita

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun ConfirmarCitaScreen(medicoId: Int, fecha: String, hora: String, onCitaAgendada: (Int) -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Confirmar cita ($medicoId, $fecha, $hora)",
        acciones = listOf("Agendar cita" to { onCitaAgendada(1) }, "Volver" to onBack)
    )
}
