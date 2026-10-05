package com.flores.saludplus.ui.screens.citas

import androidx.compose.runtime.Composable
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.MIS_CITAS
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: citasDelUsuario

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun MisCitasScreen(onDetalle: (Int) -> Unit, onNavegar: (String) -> Unit) {
    PantallaEnConstruccion(
        titulo = "Mis citas",
        acciones = listOf("Detalle de cita" to { onDetalle(1) }, "Inicio" to { onNavegar(Rutas.HOME) })
    )
}
