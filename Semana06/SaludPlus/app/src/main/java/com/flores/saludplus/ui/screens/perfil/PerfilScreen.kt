package com.flores.saludplus.ui.screens.perfil

import androidx.compose.runtime.Composable
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en el destino Rutas.PERFIL
// - Por ahora llama a PantallaEnConstruccion (ui/components/Componentes.kt)
// - Cuando se complete usará del Repositorio: usuarioActual, citasDelUsuario, cerrarSesion

// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun PerfilScreen(onNavegar: (String) -> Unit, onCerrarSesion: () -> Unit) {
    PantallaEnConstruccion(
        titulo = "Perfil",
        acciones = listOf("Cerrar sesión" to onCerrarSesion, "Inicio" to { onNavegar(Rutas.HOME) })
    )
}
