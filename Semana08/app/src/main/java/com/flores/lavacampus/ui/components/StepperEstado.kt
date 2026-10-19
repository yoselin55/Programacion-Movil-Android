package com.flores.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido

private val etapas = listOf(
    EstadoPedido.RECIBIDO,
    EstadoPedido.EN_PROCESO,
    EstadoPedido.LISTO,
    EstadoPedido.ENTREGADO
)

// Linea de seguimiento dibujada con Canvas (version horizontal)
// Etapas alcanzadas: circulo lleno con check. Pendientes: circulo vacio
@Composable
fun StepperEstado(
    estado: EstadoPedido,
    colorActivo: Color,
    colorInactivo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    val actual = etapas.indexOf(estado) // CANCELADO = -1 (ninguna alcanzada)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(26.dp)) {
            val paso = size.width / etapas.size
            val cy = size.height / 2
            val radio = 10.dp.toPx()
            // Lineas entre etapas
            for (i in 0 until etapas.size - 1) {
                drawLine(
                    color = if (i < actual) colorActivo else colorInactivo,
                    start = Offset(paso * (i + 0.5f) + radio, cy),
                    end = Offset(paso * (i + 1.5f) - radio, cy),
                    strokeWidth = 2.dp.toPx()
                )
            }
            for (i in etapas.indices) {
                val c = Offset(paso * (i + 0.5f), cy)
                if (i <= actual) {
                    drawCircle(colorActivo, radio, c)
                    // Check blanco dibujado con dos lineas
                    val g = 2.dp.toPx()
                    drawLine(Color.White, Offset(c.x - 4.dp.toPx(), c.y), Offset(c.x - 1.dp.toPx(), c.y + 3.dp.toPx()), g, StrokeCap.Round)
                    drawLine(Color.White, Offset(c.x - 1.dp.toPx(), c.y + 3.dp.toPx()), Offset(c.x + 4.dp.toPx(), c.y - 3.dp.toPx()), g, StrokeCap.Round)
                } else {
                    drawCircle(Color.White, radio, c)
                    drawCircle(colorInactivo, radio, c, style = Stroke(2.dp.toPx()))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            etapas.forEachIndexed { i, e ->
                Text(
                    e.texto,
                    fontSize = 10.sp,
                    fontWeight = if (i == actual) FontWeight.Bold else FontWeight.Normal,
                    color = if (i <= actual) colorTexto else colorInactivo,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
