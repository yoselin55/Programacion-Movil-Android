package com.flores.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Dibuja con Canvas la lavadora (wash), la plancha (iron) o la camisa (shirt)
@Composable
fun IconoServicio(icono: String, color: Color, tamano: Dp = 32.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val trazo = Stroke(width = w * 0.08f)
        when (icono) {
            "iron" -> {
                // Plancha: base en cuña y asa
                val base = Path().apply {
                    moveTo(w * 0.1f, h * 0.75f)
                    lineTo(w * 0.9f, h * 0.75f)
                    cubicTo(w * 0.9f, h * 0.45f, w * 0.65f, h * 0.35f, w * 0.45f, h * 0.35f)
                    lineTo(w * 0.1f, h * 0.35f)
                    close()
                }
                drawPath(base, color, style = trazo)
                drawLine(color, Offset(w * 0.25f, h * 0.55f), Offset(w * 0.6f, h * 0.55f), w * 0.08f)
            }
            "shirt" -> {
                // Camisa: cuerpo con mangas
                val camisa = Path().apply {
                    moveTo(w * 0.35f, h * 0.15f)
                    lineTo(w * 0.1f, h * 0.3f)
                    lineTo(w * 0.2f, h * 0.48f)
                    lineTo(w * 0.28f, h * 0.43f)
                    lineTo(w * 0.28f, h * 0.85f)
                    lineTo(w * 0.72f, h * 0.85f)
                    lineTo(w * 0.72f, h * 0.43f)
                    lineTo(w * 0.8f, h * 0.48f)
                    lineTo(w * 0.9f, h * 0.3f)
                    lineTo(w * 0.65f, h * 0.15f)
                    quadraticTo(w * 0.5f, h * 0.3f, w * 0.35f, h * 0.15f)
                    close()
                }
                drawPath(camisa, color, style = trazo)
            }
            else -> {
                // Lavadora: caja, tambor y boton
                drawRoundRect(
                    color, Offset(w * 0.15f, h * 0.1f), Size(w * 0.7f, h * 0.8f),
                    CornerRadius(w * 0.1f), style = trazo
                )
                drawCircle(color, w * 0.2f, Offset(w * 0.5f, h * 0.58f), style = trazo)
                drawCircle(color, w * 0.04f, Offset(w * 0.3f, h * 0.22f))
            }
        }
    }
}
