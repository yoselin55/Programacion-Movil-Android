package com.flores.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pestanas en forma de pastilla (Activos / Historial)
@Composable
fun SelectorPestanas(
    opciones: List<String>,
    seleccionada: Int,
    onElegir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp)
    ) {
        opciones.forEachIndexed { i, texto ->
            val activa = i == seleccionada
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (activa) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onElegir(i) }
                    .padding(vertical = 11.dp)
            ) {
                Text(
                    texto,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activa) MaterialTheme.colorScheme.onPrimary else Color(0xFF64748B)
                )
            }
        }
    }
}
