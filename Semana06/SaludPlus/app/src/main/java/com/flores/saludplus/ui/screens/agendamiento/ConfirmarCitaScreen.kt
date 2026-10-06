package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.util.Fechas

// Relaciones:
// - La llama AppNavigation en Rutas.CONFIRMAR y recibe medicoId, fecha y hora de la ruta
// - Usa BarraSuperior, BotonPrincipal y FilaDetalle (Componentes.kt)
// - Llama a Repositorio.obtenerMedico y agendarCita (la fecha se guarda en ISO)
// - Usa util/Fechas.kt (Fase 2) para mostrar la fecha en español
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

    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""

    // Rango de la consulta: la hora elegida hasta 30 minutos después
    val (h, m) = hora.split(":").map { it.toInt() }
    val fin = h * 60 + m + 30
    val rangoHora = "$hora a %02d:%02d".format(fin / 60, fin % 60)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior("Confirmar cita", onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Médico
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
                        modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(36.dp))
                    }
                    Column {
                        Text(medico?.nombre ?: "", fontWeight = FontWeight.Bold)
                        Text(especialidad, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("CMP: ${medico?.cmp ?: ""}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Datos de la cita
            // Fase 2: la fecha llega en ISO y se muestra como "Martes 6 de octubre 2026"
            FilaDetalle(Icons.Filled.CalendarMonth, "Fecha", Fechas.textoLargoDesdeIso(fecha))
            FilaDetalle(Icons.Filled.AccessTime, "Hora", rangoHora)
            FilaDetalle(Icons.Filled.Info, "Tipo de atención", "Consulta presencial")
            FilaDetalle(Icons.Filled.LocationOn, "Dirección", "Av. Los Olivos 123, Lima")

            // Motivo opcional
            Text("Motivo de consulta (opcional)", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                placeholder = { Text("Consulta de rutina") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )

            if (horarioOcupado) {
                Text("Ese horario ya fue reservado. Vuelve y elige otro.", color = MaterialTheme.colorScheme.error)
            }

            BotonPrincipal("Agendar cita", onClick = {
                val cita = Repositorio.agendarCita(medicoId, fecha, hora, motivo.trim())
                if (cita != null) onCitaAgendada(cita.id) else horarioOcupado = true
            }, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}
