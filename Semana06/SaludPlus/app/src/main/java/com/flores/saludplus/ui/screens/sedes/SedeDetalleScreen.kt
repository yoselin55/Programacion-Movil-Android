package com.flores.saludplus.ui.screens.sedes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.TextoSecundario
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone

// Relaciones:
// - La llama AppNavigation en Rutas.SEDE_DETALLE y recibe sedeId de la ruta
// - Usa BarraSuperior, FilaDetalle y BotonPrincipal (Componentes.kt) y Repositorio.obtenerSede / especialidadesDeSede
// - "Agendar cita" abre las especialidades de esa sede (onAgendar)

// Fase 3: segundo paso: datos de la sede elegida y botón "Agendar cita" que continúa con la especialidad
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SedeDetalleScreen(sedeId: Int, onAgendar: () -> Unit, onBack: () -> Unit) {
    val sede = Repositorio.obtenerSede(sedeId)
    val especialidades = Repositorio.especialidadesDeSede(sedeId)

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(if (sede != null) "Sede ${sede.nombre}" else "Sede", onBack) }
    ) { padding ->
        if (sede == null) {
            // Sede que no existe: aviso en lugar de una pantalla vacía
            Text(
                "No encontramos esta sede. Vuelve y elige otra.",
                fontSize = 18.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(padding).padding(20.dp)
            )
            return@Scaffold
        }
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fase 3: foto de la sede
                FotoSede(sede.nombre, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)))
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    FilaDetalle(Icons.Filled.LocationOn, "Dirección", sede.direccion, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                    FilaDetalle(Icons.Filled.Phone, "Teléfono", sede.telefono, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                }
                Text("Especialidades en esta sede", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    especialidades.forEach { especialidad ->
                        Text(
                            especialidad.nombre,
                            fontSize = 18.sp,
                            color = AzulPrimario,
                            modifier = Modifier
                                .background(AzulClaro, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            // Fase 3: botón fijo abajo, igual que "Continuar" y "Agendar cita" de las otras pantallas
            BotonPrincipal(
                "Agendar cita",
                onClick = onAgendar,
                enabled = especialidades.isNotEmpty(),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}
