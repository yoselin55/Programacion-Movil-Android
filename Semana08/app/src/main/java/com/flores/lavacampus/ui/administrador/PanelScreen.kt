package com.flores.lavacampus.ui.administrador

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.ui.components.graficos.GraficoBarras
import com.flores.lavacampus.ui.components.graficos.GraficoDona
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.PanelViewModel
import java.time.LocalDate

// La View solo muestra las cifras del ViewModel
@Composable
fun PanelScreen(vm: PanelViewModel, onVerListos: () -> Unit) {
    val pedidosHoy by vm.pedidosHoy.collectAsState()
    val enProceso by vm.enProceso.collectAsState()
    val porRecoger by vm.porRecoger.collectAsState()
    val ingresos by vm.ingresosSemana.collectAsState()
    val pagados by vm.pedidosPagadosSemana.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porServicio by vm.porServicio.collectAsState()
    val primario = MaterialTheme.colorScheme.primary
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val hoy = LocalDate.now().toString().fechaCorta().replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Titulo, fecha y campana
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Panel de la lavandería", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Hoy · $hoy", fontSize = 13.sp, color = gris)
            }
            Icon(Icons.Filled.Notifications, "Avisos", tint = MaterialTheme.colorScheme.onSurface)
        }

        // 3 contadores de color
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Contador("Pedidos hoy", pedidosHoy, primario, Modifier.weight(1f))
            Contador("En proceso", enProceso, EstadoPedido.EN_PROCESO.color, Modifier.weight(1f))
            Contador("Por recoger", porRecoger, EstadoPedido.LISTO.color, Modifier.weight(1f))
        }

        // Tarjeta violeta de ingresos de la semana
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(primario)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            val sobre = MaterialTheme.colorScheme.onPrimary
            Text("Ingresos de la semana", color = sobre.copy(alpha = 0.85f), fontSize = 12.sp)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(
                    ingresos.soles(),
                    color = sobre,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "$pagados pedidos",
                    color = sobre,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        Text("Pedidos por estado", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Tarjeta {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GraficoDona(porEstado, tamano = 110.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    porEstado.forEach { d ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(d.color))
                            Text(d.etiqueta, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(start = 8.dp))
                            Text(d.valor.toInt().toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Text("Ingresos por servicio (S/)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Tarjeta {
            if (porServicio.isEmpty()) {
                Text("Aún no hay ingresos esta semana", fontSize = 12.sp, color = gris)
            } else {
                GraficoBarras(porServicio)
            }
        }

        // Alerta si hay pedidos LISTO esperando recojo
        if (porRecoger > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFDCFCE7))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)
                ) { Icon(Icons.Outlined.Lock, null, tint = EstadoPedido.LISTO.color) }
                Column(Modifier.weight(1f)) {
                    Text(
                        "$porRecoger pedidos esperan recojo",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534),
                        fontSize = 13.sp
                    )
                    Text("avisa a los estudiantes", fontSize = 11.sp, color = Color(0xFF166534))
                }
                Button(
                    onClick = onVerListos,
                    colors = ButtonDefaults.buttonColors(containerColor = EstadoPedido.LISTO.color)
                ) { Text("Ver") }
            }
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor.toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
        Text(titulo, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, textAlign = TextAlign.Center)
    }
}

@Composable
private fun Tarjeta(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) { contenido() }
    }
}
