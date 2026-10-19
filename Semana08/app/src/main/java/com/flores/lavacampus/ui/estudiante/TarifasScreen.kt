package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.textoPrecio
import com.flores.lavacampus.viewmodel.TarifasViewModel

@Composable
fun TarifasScreen(vm: TarifasViewModel, onVolver: () -> Unit) {
    val servicios by vm.servicios.collectAsState()
    val primario = MaterialTheme.colorScheme.primary
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Tarifas y servicios", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Solo servicios activos: nombre, precio por unidad y tiempo de entrega
            servicios.forEach { s ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) { IconoServicio(s.icono, primario, 32.dp) }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(s.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(s.textoPrecio(), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primario)
                            Text("Entrega en ${s.horasEntrega} h", fontSize = 11.sp, color = gris)
                        }
                    }
                }
            }

            // Horario de atencion
            Row(
                Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Outlined.Schedule, null, tint = primario)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Horario de atención", fontWeight = FontWeight.Bold, color = primario, fontSize = 13.sp)
                    Text("Lunes a sábado · 8:00 a 18:00", fontSize = 12.sp, color = gris)
                    Text("Recojo en casilleros del pabellón A", fontSize = 12.sp, color = gris)
                }
            }

            // Reglas del servicio
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF3C7))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text("Mínimo 1 kg por servicio de lavado.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                Text("Máximo 3 pedidos activos por estudiante.", fontSize = 12.sp, color = Color(0xFF92400E))
            }
        }
    }
}
