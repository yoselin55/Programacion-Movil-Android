package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.MEDICOS
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: obtenerEspecialidad, medicosPorEspecialidad, buscarMedicos

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun MedicosScreen(especialidadId: Int, onMedico: (Int) -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Médicos (especialidad $especialidadId)",
        acciones = listOf("Dra. Ana Torres" to { onMedico(1) }, "Volver" to onBack)
    )
}
