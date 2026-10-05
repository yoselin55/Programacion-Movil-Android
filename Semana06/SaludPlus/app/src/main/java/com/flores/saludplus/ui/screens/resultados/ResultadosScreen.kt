package com.flores.saludplus.ui.screens.resultados

import androidx.compose.runtime.Composable
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.RESULTADOS
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: ninguna (lista fija propia)

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun ResultadosScreen(onNavegar: (String) -> Unit) {
    PantallaEnConstruccion(
        titulo = "Resultados",
        acciones = listOf("Inicio" to { onNavegar(Rutas.HOME) })
    )
}
