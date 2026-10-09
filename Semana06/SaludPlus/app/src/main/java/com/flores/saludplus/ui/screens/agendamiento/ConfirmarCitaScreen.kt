package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.components.FotoMedico
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.LineaSeparadora
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.util.Fechas

// Relaciones:
// - La llama AppNavigation en Rutas.CONFIRMAR y recibe medicoId, fecha y hora de la ruta
// - Usa BarraSuperior, BotonPrincipal y FilaDetalle (Componentes.kt)
// - Fase 2: usa FotoMedico (ImagenPorNombre.kt) para la foto del médico
// - Fase 2: usa el color LineaSeparadora (ui/theme/Color.kt) para las líneas entre los datos de la cita
// - Llama a Repositorio.obtenerMedico y agendarCita (la fecha se guarda en ISO)
// - Fase 2: llama a Repositorio.citaDelUsuarioEn y usa mensajeCruce (FechaHoraScreen.kt) para avisar cruces
// - Usa util/Fechas.kt (Fase 2) para mostrar la fecha en español y el rango de hora (rangoHora)
// - Al agendar envía el id de la cita a Cita agendada (onCitaAgendada)

// Commit 8: resumen de la cita con motivo opcional; al confirmar se guarda en el Repositorio
@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onCitaAgendada: (Int) -> Unit,
    onBack: () -> Unit
) {
    var motivo by rememberSaveable { mutableStateOf("") }
    var horarioOcupado by rememberSaveable { mutableStateOf(false) }
    // Fase 2: aviso de cruce con otra cita del paciente (null = sin cruce)
    var avisoCruce by rememberSaveable { mutableStateOf<String?>(null) }

    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
    // Fase 3: sede donde atiende el médico (su nombre y dirección reemplazan a la dirección fija)
    val sede = medico?.let { Repositorio.obtenerSede(it.sedeId) }

    // Rango de la consulta: la hora elegida hasta 30 minutos después
    // Fase 2: se calcula con Fechas.rangoHora (igual que en Cita agendada y Detalle de cita)
    val rangoHora = Fechas.rangoHora(hora)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior("Confirmar cita", onBack) }
    ) { padding ->
        // Fase 2: la pantalla ocupa todo el alto: arriba el contenido y abajo, fijo, el botón
        // "Agendar cita" (igual que "Continuar" en Seleccionar fecha y hora)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fase 2: contenido con al menos el alto disponible; el campo de motivo crece para llenar
            // el espacio libre y, si el teclado o una pantalla baja no dejan sitio, se desplaza
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Médico
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AzulClaro),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Fase 2: tarjeta más alta (padding vertical 22dp, foto de 84dp y nombre de 20sp)
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Fase 2: foto del médico por nombre (dra_ana_torres...) o silueta de respaldo
                            FotoMedico(medico?.nombre ?: "", 84.dp)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(medico?.nombre ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                // Fase 2: especialidad 18sp y código 16sp
                                Text(especialidad, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Código: ${medico?.codigo ?: ""}", fontSize = 16.sp)
                            }
                        }
                    }

                    // Datos de la cita
                    // Fase 2: la fecha llega en ISO y se muestra como "Martes 6 de octubre 2026"
                    // Fase 2: las 4 filas (Fecha a Tipo de atención) se reparten el alto libre de la pantalla;
                    // cada una ocupa la misma altura y después de cada dato hay una línea separadora
                    val datos = listOf(
                        Triple(Icons.Filled.CalendarMonth, "Fecha", Fechas.textoLargoDesdeIso(fecha)),
                        Triple(Icons.Filled.AccessTime, "Hora", rangoHora),
                        Triple(Icons.Filled.Info, "Tipo de atención", "Consulta presencial"),
                        Triple(Icons.Filled.LocationOn, "Sede", sede?.let { "${it.nombre} - ${it.direccion}" } ?: "")
                    )
                    Column(modifier = Modifier.weight(1f).heightIn(min = 280.dp)) {
                        datos.forEach { (icono, titulo, valor) ->
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // Fase 2: título 18sp y valor 22sp
                                FilaDetalle(icono, titulo, valor, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                            }
                            // Fase 2: línea de 1.5dp después de cada dato (Fecha, Hora, Sede y Tipo)
                            HorizontalDivider(thickness = 1.5.dp, color = LineaSeparadora)
                        }
                    }

                    // Motivo opcional
                    // Fase 2: etiqueta de 18sp y texto del campo de 18sp
                    Text("Motivo de consulta (opcional)", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        placeholder = { Text("Consulta de rutina", fontSize = 18.sp) },
                        textStyle = TextStyle(fontSize = 18.sp),
                        shape = RoundedCornerShape(12.dp),
                        // Fase 2: campo bajo (80dp de alto); el espacio libre lo usan los datos de la cita
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )

                    if (horarioOcupado) {
                        Text("Ese horario ya fue reservado. Vuelve y elige otro.", fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    }
                    // Fase 2: aviso en rojo si el paciente ya tiene otra cita a esa fecha y hora
                    avisoCruce?.let { Text(it, fontSize = 16.sp, color = RojoAviso) }
                }
            }

            // Fase 2: botón fijo abajo, fuera de la zona desplazable
            BotonPrincipal("Agendar cita", onClick = {
                // Fase 2: antes de agendar comprueba si el paciente ya tiene una cita a esa fecha y hora
                val cruce = Repositorio.citaDelUsuarioEn(fecha, hora)
                if (cruce != null) {
                    avisoCruce = mensajeCruce(cruce)
                    horarioOcupado = false
                } else {
                    val cita = Repositorio.agendarCita(medicoId, fecha, hora, motivo.trim())
                    if (cita != null) onCitaAgendada(cita.id) else horarioOcupado = true
                }
            }, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}
