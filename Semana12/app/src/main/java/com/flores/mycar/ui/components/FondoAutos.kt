package com.flores.mycar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform

/**
 * Fondo ilustrado de Mycar: degradado con un auto deportivo grande, lineas de velocidad y una carretera.
 * Esta dibujado con codigo, usa los colores del tema y se ve bien en modo claro y oscuro.
 */
@Composable
fun FondoAutos(modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val tinta = colores.onBackground
    val fondo = colores.background

    Canvas(modifier) {
        val w = size.width
        val h = size.height

        // Cielo: degradado del fondo hacia la superficie.
        drawRect(Brush.verticalGradient(listOf(fondo, colores.surfaceContainerHigh, colores.surfaceContainerHighest)))

        // Resplandor suave detras del auto.
        drawCircle(
            brush = Brush.radialGradient(
                listOf(tinta.copy(alpha = 0.10f), tinta.copy(alpha = 0f)),
                center = Offset(w * 0.55f, h * 0.90f),
                radius = w * 0.8f
            ),
            radius = w * 0.8f,
            center = Offset(w * 0.55f, h * 0.90f)
        )

        // Lineas de velocidad.
        val lineas = listOf(0.83f to 0.30f, 0.86f to 0.22f, 0.89f to 0.36f, 0.92f to 0.18f)
        lineas.forEachIndexed { i, (y, largo) ->
            val yy = h * y
            val x0 = w * (0.02f + 0.05f * (i % 2))
            drawLine(
                color = tinta.copy(alpha = 0.07f),
                start = Offset(x0, yy),
                end = Offset(x0 + w * largo, yy),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }

        // El auto se dibuja en un lienzo de 1000 x 400 y se escala al ancho de la pantalla.
        val escala = w * 1.0f / 1000f
        val baseY = h * 0.965f - 392f * escala

        val carroceria = Path().apply {
            moveTo(40f, 300f)
            cubicTo(40f, 262f, 70f, 242f, 130f, 234f)
            lineTo(330f, 216f)
            cubicTo(385f, 168f, 455f, 122f, 565f, 112f)
            lineTo(680f, 110f)
            cubicTo(765f, 114f, 835f, 162f, 885f, 206f)
            cubicTo(940f, 216f, 975f, 232f, 981f, 266f)
            lineTo(985f, 300f)
            close()
        }
        val ventana = Path().apply {
            moveTo(395f, 204f)
            cubicTo(440f, 156f, 500f, 130f, 566f, 126f)
            lineTo(672f, 124f)
            cubicTo(735f, 130f, 792f, 168f, 828f, 202f)
            close()
        }

        withTransform({
            translate(left = w * 0.0f, top = baseY)
            scale(scaleX = escala, scaleY = escala, pivot = Offset.Zero)
        }) {
            drawPath(carroceria, tinta.copy(alpha = 0.13f))
            drawPath(ventana, fondo.copy(alpha = 0.55f))
            // Faro y linea lateral.
            drawLine(tinta.copy(alpha = 0.10f), Offset(120f, 262f), Offset(940f, 262f), strokeWidth = 3f / escala * 1.2f)
            // Ruedas.
            for (cx in listOf(250f, 780f)) {
                drawCircle(fondo.copy(alpha = 0.95f), radius = 70f, center = Offset(cx, 302f))
                drawCircle(tinta.copy(alpha = 0.22f), radius = 54f, center = Offset(cx, 302f))
                drawCircle(fondo.copy(alpha = 0.85f), radius = 28f, center = Offset(cx, 302f))
                drawCircle(tinta.copy(alpha = 0.18f), radius = 10f, center = Offset(cx, 302f))
            }
        }

        // Carretera.
        val yRuta = baseY + 392f * escala
        drawLine(tinta.copy(alpha = 0.18f), Offset(0f, yRuta), Offset(w, yRuta), strokeWidth = 4f)
        var x = 0f
        while (x < w) {
            drawLine(tinta.copy(alpha = 0.12f), Offset(x, yRuta + 40f), Offset(x + 70f, yRuta + 40f), strokeWidth = 5f, cap = StrokeCap.Round)
            x += 130f
        }
    }
}
