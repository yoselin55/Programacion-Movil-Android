package com.flores.saludplus.ui.screens.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.TarjetaCita

// Relaciones:
// - La llama AppNavigation en Rutas.MIS_CITAS
// - Usa BarraInferior y TarjetaCita (Componentes.kt)
// - Llama a Repositorio.citasDelUsuario, obtenerMedico y obtenerEspecialidad
// - Al tocar una cita envía su id al Detalle (onDetalle)

// Commit 9: lista de citas del usuario, con mensaje cuando no hay ninguna
@Composable
fun MisCitasScreen(onDetalle: (Int) -> Unit, onNavegar: (String) -> Unit) {
    val citas = Repositorio.citasDelUsuario()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BarraInferior(Rutas.MIS_CITAS, onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Mis citas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            if (citas.isEmpty()) {
                // Lista vacía
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Aún no tienes citas agendadas.\nAgenda una desde el Inicio.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(citas, key = { it.id }) { cita ->
                        val medico = Repositorio.obtenerMedico(cita.medicoId)
                        val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
                        TarjetaCita(cita, medico?.nombre ?: "", especialidad, onClick = { onDetalle(cita.id) })
                    }
                }
            }
        }
    }
}
