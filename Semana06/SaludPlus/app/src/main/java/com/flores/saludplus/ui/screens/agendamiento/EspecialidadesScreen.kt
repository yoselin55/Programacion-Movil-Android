package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.ESPECIALIDADES
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: buscarEspecialidades

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun EspecialidadesScreen(onEspecialidad: (Int) -> Unit, onBack: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Especialidades",
        acciones = listOf("Ginecología" to { onEspecialidad(3) }, "Volver" to onBack)
    )
}
