package com.flores.saludplus.ui.screens.home

import androidx.compose.runtime.Composable
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.HOME
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: usuarioActual, especialidadesDestacadas

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun HomeScreen(onAgendar: () -> Unit, onEspecialidad: (Int) -> Unit, onNotificaciones: () -> Unit, onNavegar: (String) -> Unit) {
    PantallaEnConstruccion(
        titulo = "Inicio",
        acciones = listOf("Agendar cita" to onAgendar, "Notificaciones" to onNotificaciones, "Mis citas" to { onNavegar(Rutas.MIS_CITAS) }, "Resultados" to { onNavegar(Rutas.RESULTADOS) }, "Perfil" to { onNavegar(Rutas.PERFIL) })
    )
}
