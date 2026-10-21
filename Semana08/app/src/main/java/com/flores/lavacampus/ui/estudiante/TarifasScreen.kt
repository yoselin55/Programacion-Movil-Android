package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.fotoDeServicio
import com.flores.lavacampus.ui.components.textoPrecio
import com.flores.lavacampus.viewmodel.TarifasViewModel

@Composable
fun TarifasScreen(vm: TarifasViewModel, onVolver: () -> Unit) {
    val servicios by vm.servicios.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Tarifas y servicios", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner de entrada con la foto de las toallas
            FotoConDegradado(
                R.drawable.foto_toallas,
                Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(22.dp))
            ) {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text("Elige tu servicio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        "Tu ropa limpia te espera en un casillero",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Solo servicios activos: una foto grande por servicio con su precio y tiempo de entrega
            servicios.forEach { s ->
                FotoConDegradado(
                    fotoDeServicio(s.icono),
                    Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(22.dp)),
                    intensidad = 0.9f
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.92f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Outlined.Timer, null, tint = Color(0xFF1E293B), modifier = Modifier.size(15.dp))
                            Text("Entrega en ${s.horasEntrega} h", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }
                        Box(
                            Modifier.size(46.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) { IconoServicio(s.icono, MaterialTheme.colorScheme.primary, 28.dp) }
                    }
                    Column(
                        Modifier.align(Alignment.BottomStart).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(s.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(s.textoPrecio(), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            Horario()
            Reglas()
        }
    }
}

// Horario de atencion: foto arriba y cuatro datos en recuadros
@Composable
private fun Horario() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.fillMaxWidth().height(110.dp)) {
            Image(
                painter = painterResource(R.drawable.foto_canasta),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            val primario = MaterialTheme.colorScheme.primary
            Box(Modifier.fillMaxSize().background(primario.copy(alpha = 0.55f)))
            Row(
                Modifier.align(Alignment.BottomStart).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Schedule, null, tint = Color.White)
                Text("Horario de atención", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Dato(Icons.Outlined.CalendarMonth, "Días", "Lunes a sábado", Modifier.weight(1f))
                Dato(Icons.Outlined.Schedule, "Horas", "8:00 a 18:00", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Dato(Icons.Outlined.Place, "Casilleros", "Pabellón A", Modifier.weight(1f))
                Dato(Icons.Outlined.Info, "Domingo", "Sin atención", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, titulo: String, valor: String, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) { Icon(icono, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp)) }
        Column {
            Text(titulo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Reglas del servicio con los colores del tema (se ven bien en claro y oscuro)
@Composable
private fun Reglas() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            val color = MaterialTheme.colorScheme.onSecondaryContainer
            Text("Mínimo 1 kg por servicio de lavado.", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
            Text("Máximo 3 pedidos activos por estudiante.", fontSize = 13.sp, color = color)
        }
    }
}
