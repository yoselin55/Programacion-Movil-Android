package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.theme.VerdeDisponible

// Relaciones:
// - La llama AppNavigation en Rutas.CITA_EXITOSA y recibe citaId de la ruta
// - Usa BotonPrincipal y FilaDetalle (Componentes.kt)
// - Llama a Repositorio.obtenerCita y obtenerMedico
// - Sus botones llevan a Mis citas (onVerMisCitas) y a Inicio (onIrInicio)

// Commit 8: confirmación con el resumen de la cita agendada
@Composable
fun CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onIrInicio: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = VerdeDisponible,
                modifier = Modifier.size(96.dp)
            )
            Text("¡Cita agendada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Te esperamos en la clínica",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            // Resumen de la cita
            if (cita != null && medico != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilaDetalle(Icons.Filled.Person, especialidad, medico.nombre)
                        FilaDetalle(Icons.Filled.CalendarMonth, "Fecha", cita.fecha)
                        FilaDetalle(Icons.Filled.AccessTime, "Hora", cita.hora)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Ver mis citas", onClick = onVerMisCitas)
            TextButton(onClick = onIrInicio) { Text("Ir al inicio") }
        }
    }
}
