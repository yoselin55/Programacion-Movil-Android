package com.flores.saludplus.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.TarjetaAccion
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.Morado
import com.flores.saludplus.ui.theme.MoradoClaro
import com.flores.saludplus.ui.theme.Naranja
import com.flores.saludplus.ui.theme.NaranjaClaro
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible

// Relaciones:
// - La llama AppNavigation en Rutas.HOME
// - Usa TarjetaAccion y BarraInferior (Componentes.kt) y Repositorio (usuarioActual, especialidadesDestacadas)
// - Navega a Especialidades, Médicos, Notificaciones y a Mis citas, Perfil y Resultados

// Commit 4: pantalla de Inicio con saludo, tarjetas y especialidades destacadas
@Composable
fun HomeScreen(
    onAgendar: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onNotificaciones: () -> Unit,
    onNavegar: (String) -> Unit
) {
    // Primer nombre del usuario con sesión iniciada
    val nombre = Repositorio.usuarioActual?.nombre?.substringBefore(" ") ?: ""

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // Commit 5: barra inferior de navegación
        bottomBar = { BarraInferior(Rutas.HOME, onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saludo y campana de notificaciones
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("¡Hola, $nombre!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "¿Qué deseas hacer hoy?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onNotificaciones) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                }
            }

            // Tarjetas de acceso rápido en 2 filas
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Agendar cita", Icons.Filled.CalendarMonth, AzulClaro, AzulPrimario, onAgendar, Modifier.weight(1f))
                TarjetaAccion("Mis citas", Icons.Filled.Event, VerdeClaro, VerdeDisponible, { onNavegar(Rutas.MIS_CITAS) }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Mis datos", Icons.Filled.Person, MoradoClaro, Morado, { onNavegar(Rutas.PERFIL) }, Modifier.weight(1f))
                TarjetaAccion("Resultados", Icons.Filled.Description, NaranjaClaro, Naranja, { onNavegar(Rutas.RESULTADOS) }, Modifier.weight(1f))
            }

            // Especialidades destacadas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Especialidades destacadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onAgendar) { Text("Ver todas") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Repositorio.especialidadesDestacadas()) { especialidad ->
                    EspecialidadDestacada(especialidad, onClick = { onEspecialidad(especialidad.id) })
                }
            }
        }
    }
}

// Tarjeta pequeña de una especialidad para el LazyRow
@Composable
private fun EspecialidadDestacada(especialidad: Especialidad, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(110.dp).clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(especialidad.icono, contentDescription = null, tint = AzulPrimario)
            }
            Text(
                especialidad.nombre,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
