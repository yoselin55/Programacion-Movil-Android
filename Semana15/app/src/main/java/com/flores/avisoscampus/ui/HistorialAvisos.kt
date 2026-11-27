package com.flores.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.avisoscampus.model.Aviso
import com.flores.avisoscampus.model.Origen

// No conoce el ViewModel: solo dibuja la lista que recibe.
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (avisos.isEmpty()) {
                Text("Sin avisos todavía")
            } else {
                // Column + forEach (son pocas filas): la pantalla ya se desplaza
                avisos.take(20).forEach { aviso ->
                    FilaAviso(aviso)
                }
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F2F7), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = aviso.origen.texto,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(colorOrigen(aviso.origen), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(aviso.titulo, fontWeight = FontWeight.Bold)
            Text(
                text = aviso.texto,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = aviso.hora,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(ANCHO_HORA)
        )
    }
}

private val ANCHO_HORA = 62.dp

private fun colorOrigen(origen: Origen): Color = when (origen) {
    Origen.BOTON -> Color(0xFF2F7FD0)
    Origen.RECORDATORIO -> Color(0xFFE09A12)
    Origen.MODO_AVION -> Color(0xFF7B4FC9)
    Origen.CARGADOR -> Color(0xFF2E9E5B)
}
