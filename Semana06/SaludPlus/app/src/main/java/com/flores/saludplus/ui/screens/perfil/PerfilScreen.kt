package com.flores.saludplus.ui.screens.perfil

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.PantallaEnConstruccion

// Relaciones:
// - La llama AppNavigation en Rutas.PERFIL
// - Llama a BarraInferior y PantallaEnConstruccion (ui/components/Componentes.kt)

// Commit 5: marco con barra inferior; el contenido se hace en un commit posterior
// TODO: implementar la pantalla según el diseño y quitar PantallaEnConstruccion
@Composable
fun PerfilScreen(onNavegar: (String) -> Unit, onCerrarSesion: () -> Unit) {
    Scaffold(bottomBar = { BarraInferior(Rutas.PERFIL, onNavegar) }) { padding ->
        Box(Modifier.padding(padding)) {
            PantallaEnConstruccion(
                titulo = "Perfil",
                acciones = listOf("Cerrar sesión" to onCerrarSesion)
            )
        }
    }
}
