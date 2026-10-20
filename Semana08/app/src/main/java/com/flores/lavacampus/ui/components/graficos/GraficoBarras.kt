package com.flores.lavacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.DatoGrafico
import java.util.Locale

// Barras verticales dibujadas con Canvas: valor arriba, barra al centro y etiqueta abajo
@Composable
fun GraficoBarras(datos: List<DatoGrafico>, modifier: Modifier = Modifier) {
    val maximo = datos.maxOfOrNull { it.valor } ?: 0f
    val altoMaximo = 84.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEach { d ->
            val alto = if (maximo > 0f) maxOf(altoMaximo * (d.valor / maximo), 8.dp) else 8.dp
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    // 72.0 se muestra "72" y 7.5 se muestra "7.5"
                    if (d.valor % 1f == 0f) d.valor.toInt().toString() else String.format(Locale.US, "%.1f", d.valor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Canvas(Modifier.width(46.dp).height(alto)) {
                    drawRoundRect(d.color, cornerRadius = CornerRadius(8.dp.toPx()))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    d.etiqueta,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
