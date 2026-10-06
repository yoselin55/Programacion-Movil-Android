package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.ChipSeleccion
import com.flores.saludplus.ui.components.FotoMedico
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.util.Fechas
import java.time.LocalDate

// Relaciones:
// - La llama AppNavigation en Rutas.FECHA_HORA y recibe medicoId de la ruta
// - Usa BarraSuperior, ChipSeleccion y BotonPrincipal (Componentes.kt)
// - Fase 2: usa FotoMedico (ImagenPorNombre.kt) para la foto del médico
// - Llama a Repositorio.obtenerMedico y horariosDisponibles
// - Usa util/Fechas.kt (Fase 2) para generar los días hábiles, el mes y el año con LocalDate
// - Al continuar envía fecha (ISO "yyyy-MM-dd") y hora a Confirmar cita (onContinuar)

// Commit 7: elección de día y hora; los horarios salen de horariosDisponibles
// Fase 2: los días se generan con LocalDate en lugar de una lista fija
@Composable
fun FechaHoraScreen(medicoId: Int, onContinuar: (String, String) -> Unit, onBack: () -> Unit) {
    // Fase 2: fecha de hoy, se calcula una sola vez al abrir la pantalla
    val hoy = remember { LocalDate.now() }
    // Fase 2: semanas avanzadas desde la actual (0 = semana actual, nunca negativo)
    var desplazamientoSemana by rememberSaveable { mutableIntStateOf(0) }
    // Fase 2: 5 días hábiles de la semana mostrada (sin fines de semana ni días pasados)
    val dias = Fechas.semana(hoy, desplazamientoSemana)
    // Fase 2: día elegido como String ISO; empieza en el primer día hábil de la semana
    var fechaSeleccionada by rememberSaveable { mutableStateOf<String?>(dias.first().toString()) }
    // Hora elegida; null mientras no se toque ningún horario
    var horaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }

    // Fase 2: cambia de semana, selecciona su primer día y reinicia la hora
    fun cambiarSemana(nuevoDesplazamiento: Int) {
        desplazamientoSemana = nuevoDesplazamiento
        fechaSeleccionada = Fechas.semana(hoy, nuevoDesplazamiento).first().toString()
        horaSeleccionada = null
    }

    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
    // Horarios del día elegido; se recalculan en cada recomposición con horariosDisponibles,
    // así las horas ya reservadas nunca aparecen aunque se cambie de día o semana y se vuelva
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
                // Fase 2: tarjeta un poco más alta (padding vertical 18dp y foto de 68dp)
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Fase 2: foto del médico por nombre (dra_ana_torres...) o silueta de respaldo
                    FotoMedico(medico?.nombre ?: "", 68.dp)
                    Column {
                        Text(medico?.nombre ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(especialidad, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Fase 2: mes y año de la semana mostrada; "<" retrocede y ">" avanza una semana
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Fase 2: "<" deshabilitada en la semana actual (no se puede ir al pasado)
                IconButton(
                    onClick = { cambiarSemana(desplazamientoSemana - 1) },
                    enabled = desplazamientoSemana > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Semana anterior")
                }
                Text(
                    Fechas.mesYAnio(dias.first()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                // Fase 2: ">" siempre habilitada, avanza una semana
                IconButton(onClick = { cambiarSemana(desplazamientoSemana + 1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Semana siguiente")
                }
            }

            // Fase 2: días hábiles generados con LocalDate; al cambiar de día se reinicia la hora
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                dias.forEach { fecha ->
                    ChipSeleccion(
                        texto = Fechas.diaCorto(fecha),
                        subtitulo = fecha.dayOfMonth.toString(),
                        seleccionado = fecha.toString() == fechaSeleccionada,
                        onClick = { fechaSeleccionada = fecha.toString(); horaSeleccionada = null },
                        modifier = Modifier.weight(1f),
                        // Fase 2: los días más altos (100dp) que los botones de hora
                        alto = 100.dp
                    )
                }
            }

            // Horarios disponibles en cuadrícula de 3 columnas
            if (fechaSeleccionada == null) {
                Text("Elige un día para ver los horarios", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (horarios.isEmpty()) {
                Text("No hay horarios disponibles este día", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Fase 2: la cuadrícula ocupa TODO el espacio libre hasta el botón "Continuar" (solo
            // quedan los 16dp de separación). El alto de cada botón se reparte entre las filas de
            // horariosBase (3 de 3), mínimo 52dp; no cambia aunque haya horas reservadas
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val filas = (Repositorio.horariosBase.size + 2) / 3
                val altoHora = ((maxHeight - 8.dp * (filas - 1)) / filas).coerceAtLeast(52.dp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    // Fase 2: separación horizontal de 6dp para que cada botón sea un poco más ancho
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(horarios, key = { it }) { hora ->
                        ChipSeleccion(
                            texto = hora,
                            seleccionado = hora == horaSeleccionada,
                            onClick = { horaSeleccionada = hora },
                            alto = altoHora,
                            tamanoTexto = 20.sp
                        )
                    }
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
