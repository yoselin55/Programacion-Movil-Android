package com.flores.saludplus.ui.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.Fechas

// Relaciones:
// - La llama AppNavigation en el destino Rutas.NOTIFICACIONES (desde la campana de Inicio)
// - Fase 2: usa BarraSuperior (Componentes.kt)
// - Fase 2: llama a Repositorio.citasDelUsuario y obtenerMedico
// - Fase 2: usa util/Fechas.kt (textoCortoDesdeIso) para la fecha del mensaje

// Fase 2: dibuja un recordatorio por cada cita del usuario, o "No tienes notificaciones" si no hay citas
@Composable
fun NotificacionesScreen(onBack: () -> Unit) {
    // Fase 2: convierte cada cita en su mensaje con map
    val mensajes = Repositorio.citasDelUsuario().map { cita ->
        val medico = Repositorio.obtenerMedico(cita.medicoId)?.nombre ?: "tu médico"
        "Tienes una cita con $medico el ${Fechas.textoCortoDesdeIso(cita.fecha)} a las ${cita.hora}"
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior("Notificaciones", onBack) }
    ) { padding ->
        if (mensajes.isEmpty()) {
            // Fase 2: estado vacío centrado y desplazable (heightIn(min = maxHeight) permite centrarlo)
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No tienes notificaciones", fontSize = 20.sp, color = TextoSecundario, textAlign = TextAlign.Center)
                }
            }
        } else {
            // Fase 2: la lista ocupa toda la pantalla y deja 24dp abajo para el último elemento
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(mensajes) { mensaje -> TarjetaNotificacion(mensaje) }
            }
        }
    }
}

// Fase 2: dibuja una tarjeta con la campana en un círculo azul claro y el mensaje de 18sp
@Composable
private fun TarjetaNotificacion(mensaje: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(AzulClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(30.dp))
        }
        Text(mensaje, fontSize = 18.sp, color = TextoPrincipal, modifier = Modifier.weight(1f))
    }
}
