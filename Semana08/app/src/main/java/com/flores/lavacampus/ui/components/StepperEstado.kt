package com.flores.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

// Version vertical (pantalla Detalle): una fila por etapa con un texto a la derecha
@Composable
fun StepperEstadoVertical(
    estado: EstadoPedido,
    colorActivo: Color,
    colorInactivo: Color,
    colorTexto: Color,
    colorFondo: Color = Color.White, // fondo de los circulos pendientes (la superficie de la tarjeta)
    textoDerecha: (EstadoPedido) -> String,
    modifier: Modifier = Modifier
) {
    val actual = etapas.indexOf(estado)

    Column(modifier = modifier.fillMaxWidth()) {
        etapas.forEachIndexed { i, etapa ->
            val alcanzada = i <= actual
            Row(modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Canvas(modifier = Modifier.width(32.dp).height(52.dp)) {
                    val cx = size.width / 2
                    val cy = 14.dp.toPx()
                    val radio = 11.dp.toPx()
                    // Linea hacia la etapa siguiente
                    if (i < etapas.size - 1) {
                        drawLine(
                            color = if (i < actual) colorActivo else colorInactivo,
                            start = Offset(cx, cy + radio),
                            end = Offset(cx, size.height + 14.dp.toPx() - radio),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    val c = Offset(cx, cy)
                    if (alcanzada) {
                        drawCircle(colorActivo, radio, c)
                        val g = 2.dp.toPx()
                        drawLine(Color.White, Offset(cx - 4.dp.toPx(), cy), Offset(cx - 1.dp.toPx(), cy + 3.dp.toPx()), g, StrokeCap.Round)
                        drawLine(Color.White, Offset(cx - 1.dp.toPx(), cy + 3.dp.toPx()), Offset(cx + 4.dp.toPx(), cy - 3.dp.toPx()), g, StrokeCap.Round)
                    } else {
                        drawCircle(colorFondo, radio, c)
                        drawCircle(colorInactivo, radio, c, style = Stroke(2.dp.toPx()))
                    }
                }
                Row(
                    modifier = Modifier.weight(1f).padding(start = 10.dp, top = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        etapa.texto,
                        fontSize = 14.sp,
                        fontWeight = if (i == actual) FontWeight.Bold else FontWeight.Normal,
                        color = if (alcanzada) colorTexto else colorInactivo,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        textoDerecha(etapa),
                        fontSize = 12.sp,
                        color = if (alcanzada) colorTexto.copy(alpha = 0.7f) else colorInactivo
                    )
                }
            }
        }
    }
}
