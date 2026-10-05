package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.ChipSeleccion
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario

// Relaciones:
// - La llama AppNavigation en Rutas.FECHA_HORA y recibe medicoId de la ruta
// - Usa BarraSuperior, ChipSeleccion y BotonPrincipal (Componentes.kt)
// - Llama a Repositorio.obtenerMedico y horariosDisponibles
// - Al continuar envía fecha y hora a Confirmar cita (onContinuar)

// Días fijos de la semana (Fase 1): etiqueta, número y fecha en formato ISO
private val diasSemana = listOf(
    Triple("Lun", "15", "2026-09-15"),
    Triple("Mar", "16", "2026-09-16"),
    Triple("Mié", "17", "2026-09-17"),
    Triple("Jue", "18", "2026-09-18"),
    Triple("Vie", "19", "2026-09-19")
)

// Commit 7: elección de día y hora; los horarios salen de horariosDisponibles
@Composable
fun FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit, onBack: () -> Unit) {
    var fechaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }
    var horaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }

    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
    // Horarios del día elegido (los ya reservados no aparecen)
    val horarios = fechaSeleccionada?.let { Repositorio.horariosDisponibles(medicoId, it) } ?: emptyList()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior("Seleccionar fecha y hora", onBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Datos del médico elegido
            Card(
                colors = CardDefaults.cardColors(containerColor = AzulClaro),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = AzulPrimario)
                    }
                    Column {
                        Text(medico?.nombre ?: "", fontWeight = FontWeight.Bold)
                        Text(especialidad, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Mes (las flechas se activan en la Fase 2 con el calendario dinámico)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}, enabled = false) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Semana anterior")
                }
                Text(
                    "Setiembre 2026",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {}, enabled = false) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Semana siguiente")
                }
            }

            // Días de la semana; al cambiar de día se reinicia la hora
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                diasSemana.forEach { (etiqueta, numero, fecha) ->
                    ChipSeleccion(
                        texto = etiqueta,
                        subtitulo = numero,
                        seleccionado = fecha == fechaSeleccionada,
                        onClick = { fechaSeleccionada = fecha; horaSeleccionada = null },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Horarios disponibles en cuadrícula de 3 columnas
            if (fechaSeleccionada == null) {
                Text("Elige un día para ver los horarios", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (horarios.isEmpty()) {
                Text("No hay horarios disponibles este día", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(horarios, key = { it }) { hora ->
                    ChipSeleccion(
                        texto = hora,
                        seleccionado = hora == horaSeleccionada,
                        onClick = { horaSeleccionada = hora }
                    )
                }
            }

            // Continuar solo con día y hora elegidos
            BotonPrincipal(
                "Continuar",
                enabled = fechaSeleccionada != null && horaSeleccionada != null,
                onClick = { onContinuar(fechaSeleccionada!!, horaSeleccionada!!) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
