package com.flores.lavacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.DatoGrafico

// Dona dibujada con Canvas: un arco por dato y el total en el centro
@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    tamano: Dp = 130.dp,
    textoCentro: String = "pedidos"
) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()

    Box(modifier = modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(tamano)) {
            val grosor = size.width * 0.17f
            val lado = size.width - grosor
            val arco = Size(lado, lado)
            val esquina = Offset(grosor / 2, grosor / 2)
            if (total <= 0f) {
                // Sin datos: anillo gris
                drawArc(Color(0xFFE2E8F0), 0f, 360f, false, esquina, arco, style = Stroke(grosor))
            } else {
                var inicio = -90f
                datos.filter { it.valor > 0 }.forEach { d ->
                    val barrido = d.valor / total * 360f
                    drawArc(d.color, inicio, barrido, false, esquina, arco, style = Stroke(grosor))
                    inicio += barrido
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(total.toInt().toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                textoCentro,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
