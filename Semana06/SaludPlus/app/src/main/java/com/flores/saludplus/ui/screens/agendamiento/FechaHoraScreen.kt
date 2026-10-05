package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.FECHA_HORA
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: obtenerMedico, horariosDisponibles

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Fecha y hora (médico $medicoId)",
        acciones = listOf("Continuar" to { onContinuar("2026-10-06", "09:30") }, "Volver" to onBack)
    )
}
