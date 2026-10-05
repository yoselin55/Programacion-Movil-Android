package com.flores.saludplus.ui.screens.citas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en Rutas.MIS_CITAS
// - Llama a BarraInferior y PantallaEnConstruccion (ui/components/Componentes.kt)

// Commit 5: marco con barra inferior; el contenido se hace en un commit posterior
// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun MisCitasScreen(onDetalle: (Int) -> Unit, onNavegar: (String) -> Unit) {
    Scaffold(bottomBar = { BarraInferior(Rutas.MIS_CITAS, onNavegar) }) { padding ->
        Box(Modifier.padding(padding)) {
            PantallaEnConstruccion(
                titulo = "Mis citas",
                acciones = listOf("Detalle de cita" to { onDetalle(1) })
            )
        }
    }
}
