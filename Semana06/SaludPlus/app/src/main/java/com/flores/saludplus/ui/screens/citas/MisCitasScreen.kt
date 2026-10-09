package com.flores.saludplus.ui.screens.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.components.EncabezadoConMenu
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.TarjetaCita
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en Rutas.MIS_CITAS
// - Usa BarraInferior y TarjetaCita (Componentes.kt); Fase 2: también BotonPrincipal
// - Llama a Repositorio.citasDelUsuario, obtenerMedico y obtenerEspecialidad
// - Al tocar una cita envía su id al Detalle (onDetalle)
// - Fase 3: es la "Agenda" del menú lateral (onMenu); el botón del estado vacío navega a Rutas.SEDES (onNavegar)

// Commit 9: lista de citas del usuario, con mensaje cuando no hay ninguna
// Fase 2: título de 34sp, tarjetas con foto y estado vacío con botón "Agendar cita"
@Composable
fun MisCitasScreen(onDetalle: (Int) -> Unit, onNavegar: (String) -> Unit, onMenu: () -> Unit) {
    val citas = Repositorio.citasDelUsuario()

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraInferior(Rutas.MIS_CITAS, onNavegar) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            // Fase 3: botón de menú y título "Agenda" (34sp) iguales a los de Sedes y Doctores
            EncabezadoConMenu("Agenda", onMenu)

            if (citas.isEmpty()) {
                EstadoSinCitas(onAgendar = { onNavegar(Rutas.SEDES) })
            } else {
                // Fase 2: la lista ocupa el alto libre (weight) y deja 24dp sobre la barra inferior
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
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

// Fase 2: dibuja el estado vacío centrado: círculo azul claro de 140dp con calendario de 72dp,
// textos y el botón "Agendar cita" de 64dp; desplazable si la pantalla es baja
@Composable
private fun EstadoSinCitas(onAgendar: () -> Unit) {
    // Fase 2: heightIn(min = maxHeight) permite centrar verticalmente aunque la columna se desplace
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier.size(140.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(72.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Aún no tienes citas agendadas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text("Agenda una desde Sedes", fontSize = 18.sp, color = TextoSecundario, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            BotonPrincipal(
                "Agendar cita",
                onClick = onAgendar,
                alto = 64.dp,
                radio = 16.dp,
                tamanoTexto = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
