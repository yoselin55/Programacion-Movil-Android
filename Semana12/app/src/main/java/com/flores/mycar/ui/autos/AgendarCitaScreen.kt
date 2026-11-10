package com.flores.mycar.ui.autos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.domain.model.claveHorario
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.HORAS_CITA
import com.flores.mycar.ui.components.IconoAtras
import com.flores.mycar.ui.components.precioEnSoles
import com.flores.mycar.ui.components.proximosDias

/** Formulario para agendar una cita: elegir el dia, la hora y confirmar. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgendarCitaScreen(
    viewModel: AgendarCitaViewModel,
    sesion: Sesion,
    onVolver: () -> Unit,
    onVerMisCitas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoAuto by viewModel.auto.collectAsStateWithLifecycle()
    val ocupados by viewModel.ocupados.collectAsStateWithLifecycle()
    val enviando by viewModel.enviando.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val exito by viewModel.exito.collectAsStateWithLifecycle()

    val dias = remember { proximosDias(14) }
    var fecha by rememberSaveable { mutableStateOf(dias.first().first) }
    var hora by rememberSaveable { mutableStateOf("") }
    var nota by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = { IconoAtras(onVolver) }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val e = estadoAuto) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = onVolver)
                is UiState.Exito -> AnimatedContent(
                    targetState = exito,
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                    label = "cita"
                ) { cita ->
                    if (cita != null) {
                        CitaAgendada(cita, onVerMisCitas, onVolver)
                    } else {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                            ResumenAuto(e.datos)

                            Seccion("Elige el día", Icons.Filled.CalendarMonth)
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                dias.forEach { (valor, etiqueta) ->
                                    FilterChip(
                                        selected = fecha == valor,
                                        onClick = { fecha = valor; hora = "" },
                                        label = { Text(etiqueta, style = MaterialTheme.typography.titleSmall) },
                                        modifier = Modifier.height(44.dp)
                                    )
                                }
                            }

                            Seccion("Elige la hora", Icons.Filled.Schedule)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HORAS_CITA.forEach { h ->
                                    val tomada = claveHorario(fecha, h) in ocupados
                                    FilterChip(
                                        selected = hora == h,
                                        enabled = !tomada,
                                        onClick = { hora = h },
                                        label = { Text(if (tomada) "$h  Ocupada" else h, style = MaterialTheme.typography.titleSmall) },
                                        modifier = Modifier.height(44.dp)
                                    )
                                }
                            }

                            Seccion("Comentario (opcional)", Icons.Filled.EditNote)
                            OutlinedTextField(
                                value = nota,
                                onValueChange = { nota = it },
                                placeholder = { Text("Ej. Quiero ver el auto por dentro") },
                                minLines = 2,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            error?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
                            }

                            Button(
                                onClick = { viewModel.agendar(sesion, e.datos, fecha, hora, nota) },
                                enabled = hora.isNotBlank() && !enviando,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 22.dp).height(56.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                Text(
                                    if (enviando) "Agendando..." else if (hora.isBlank()) "Elige una hora" else "Confirmar cita · $fecha $hora",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, icono: ImageVector) {
    Row(Modifier.padding(top = 24.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun ResumenAuto(auto: Auto) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            FotoAuto(auto.imagenUrl, Modifier.size(width = 120.dp, height = 90.dp).clip(RoundedCornerShape(16.dp)))
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text("Cita para ver", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(auto.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(precioEnSoles(auto.precio), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

/** Pantalla de exito: un check que aparece con rebote, el dia y la hora, y botones. */
@Composable
private fun CitaAgendada(cita: Cita, onVerMisCitas: () -> Unit, onVolver: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val escala by animateFloatAsState(
        targetValue = if (visible) 1f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "check"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(110.dp).scale(escala)
        )
        Text("¡Cita agendada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
        Text(cita.autoNombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.padding(top = 20.dp)) {
            Row(Modifier.padding(horizontal = 22.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(cita.fecha, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 10.dp))
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(cita.hora, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 10.dp))
            }
        }
        Text(
            "El administrador recibió tu solicitud y la confirmará.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(onClick = onVerMisCitas, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(top = 28.dp).height(54.dp)) {
            Text("Ver mis citas")
        }
        OutlinedButton(onClick = onVolver, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(54.dp)) {
            Text("Volver al auto")
        }
    }
}
