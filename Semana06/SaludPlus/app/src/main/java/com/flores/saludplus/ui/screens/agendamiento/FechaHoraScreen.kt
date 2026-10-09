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
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.data.model.Cita
import com.flores.saludplus.util.Fechas
import java.time.LocalDate

// Relaciones:
// - La llama AppNavigation en Rutas.FECHA_HORA y recibe medicoId de la ruta
// - Usa BarraSuperior, ChipSeleccion y BotonPrincipal (Componentes.kt)
// - Fase 2: usa FotoMedico (ImagenPorNombre.kt) para la foto del médico
// - Llama a Repositorio.obtenerMedico y horariosDisponibles
// - Fase 2: llama a Repositorio.citaDelUsuarioEn para atenuar las horas en las que el paciente ya tiene cita
// - Fase 2: mensajeCruce también lo usa ConfirmarCitaScreen
// - Usa util/Fechas.kt (Fase 2) para generar los días hábiles, el mes y el año con LocalDate
// - Al continuar envía fecha (ISO "yyyy-MM-dd") y hora a Confirmar cita (onContinuar)

// Fase 2: devuelve el aviso de cruce para una cita existente del paciente, por ejemplo
// "Ya tienes una cita el Viernes 9 de octubre 2026 a las 08:30 con Dr. Ricardo Núñez. Elige otro horario."
internal fun mensajeCruce(cita: Cita): String {
    val medico = Repositorio.obtenerMedico(cita.medicoId)?.nombre ?: "otro médico"
    return "Ya tienes una cita el ${Fechas.textoLargoDesdeIso(cita.fecha)} a las ${cita.hora} " +
        "con $medico. Elige otro horario."
}

// Fase 3: primer día de la lista con horarios libres para el médico (ISO); si ninguno tiene, el primero
private fun primerDiaConHorarios(medicoId: Int, dias: List<LocalDate>): String =
    (dias.firstOrNull { Repositorio.horariosDisponibles(medicoId, it.toString()).isNotEmpty() } ?: dias.first())
        .toString()

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
    // Fase 3: día elegido como String ISO; empieza en el primer día de la semana con horarios libres
    // (si ninguno tiene, queda en el primero)
    var fechaSeleccionada by rememberSaveable { mutableStateOf<String?>(primerDiaConHorarios(medicoId, dias)) }
    // Hora elegida; null mientras no se toque ningún horario
    var horaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }
    // Fase 2: aviso de cruce al tocar una hora en la que el paciente ya tiene otra cita (null = sin aviso)
    var avisoCruce by rememberSaveable { mutableStateOf<String?>(null) }

    // Fase 2: cambia de semana, selecciona su primer día y reinicia la hora y el aviso
    fun cambiarSemana(nuevoDesplazamiento: Int) {
        desplazamientoSemana = nuevoDesplazamiento
        fechaSeleccionada = primerDiaConHorarios(medicoId, Fechas.semana(hoy, nuevoDesplazamiento))
        horaSeleccionada = null
        avisoCruce = null
    }

    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
    val sede = medico?.let { Repositorio.obtenerSede(it.sedeId)?.nombre } ?: ""
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
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(medico?.nombre ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(especialidad, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        // Fase 3: sede y días y horas en que atiende el médico
                        Text("Sede $sede", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            medico?.let { Repositorio.resumenHorario(it) } ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AzulPrimario
                        )
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
                    // Fase 3: un día sin horarios libres (el médico no atiende o ya no quedan) va atenuado
                    val sinHorarios = Repositorio.horariosDisponibles(medicoId, fecha.toString()).isEmpty()
                    ChipSeleccion(
                        texto = Fechas.diaCorto(fecha),
                        subtitulo = fecha.dayOfMonth.toString(),
                        seleccionado = fecha.toString() == fechaSeleccionada,
                        onClick = {
                            if (sinHorarios) {
                                // Fase 3: no se puede elegir; explica por qué
                                val atiende = medico?.atiende(fecha.dayOfWeek) == true
                                avisoCruce = if (atiende) "Ya no quedan horarios libres el ${Fechas.textoLargo(fecha)}."
                                else "${medico?.nombre ?: "El médico"} no atiende los ${Fechas.nombreDiaPlural(fecha.dayOfWeek)}. Elige un día disponible."
                            } else {
                                fechaSeleccionada = fecha.toString(); horaSeleccionada = null; avisoCruce = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        // Fase 2: los días más altos (100dp) que los botones de hora
                        alto = 100.dp,
                        atenuado = sinHorarios
                    )
                }
            }

            // Horarios disponibles en cuadrícula de 3 columnas
            if (fechaSeleccionada == null) {
                Text("Elige un día para ver los horarios", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (horarios.isEmpty()) {
                Text("Este día no hay horarios disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Fase 2: la cuadrícula ocupa TODO el espacio libre hasta el botón "Continuar" (solo
            // quedan los 16dp de separación). El alto de cada botón se reparte entre las filas de
            // horas máximas que atiende el médico en un día (3 de 3), mínimo 52dp; no cambia aunque haya
            // horas reservadas
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val filas = ((medico?.horasMaximasPorDia ?: 0) + 2).div(3).coerceAtLeast(1)
                val altoHora = ((maxHeight - 8.dp * (filas - 1)) / filas).coerceAtLeast(52.dp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    // Fase 2: separación horizontal de 6dp para que cada botón sea un poco más ancho
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(horarios, key = { it }) { hora ->
                        // Fase 2: cita del paciente a esa misma fecha y hora con otro médico (null = libre)
                        val cruce = fechaSeleccionada?.let { Repositorio.citaDelUsuarioEn(it, hora) }
                        ChipSeleccion(
                            texto = hora,
                            seleccionado = hora == horaSeleccionada,
                            onClick = {
                                // Fase 2: una hora con cruce no se selecciona; solo muestra el aviso
                                if (cruce != null) {
                                    avisoCruce = mensajeCruce(cruce)
                                } else {
                                    horaSeleccionada = hora
                                    avisoCruce = null
                                }
                            },
                            alto = altoHora,
                            tamanoTexto = 20.sp,
                            atenuado = cruce != null
                        )
                    }
                }
            }

            // Fase 2: aviso de cruce en rojo (16sp); minLines reserva siempre su espacio para que la
            // cuadrícula no cambie de tamaño cuando el aviso aparece o desaparece
            Text(
                avisoCruce ?: "",
                fontSize = 16.sp,
                lineHeight = 20.sp,
                minLines = 3,
                color = RojoAviso,
                modifier = Modifier.fillMaxWidth()
            )

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
