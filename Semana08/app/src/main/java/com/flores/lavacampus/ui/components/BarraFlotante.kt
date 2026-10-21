package com.flores.lavacampus.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido

// Un destino de la barra: su nombre, su icono (de contorno, y relleno cuando esta activo) y sus avisos sin leer
class ItemBarra(val texto: String, val icono: ImageVector, val iconoActivo: ImageVector, val avisos: Int = 0)

// Barra flotante con una muesca redonda sobre el destino activo, donde se apoya un punto de color.
// La muesca se desliza hasta el destino que se elige. Es un poco transparente y usa los colores del tema
@Composable
fun BarraFlotante(
    items: List<ItemBarra>,
    seleccionada: Int,
    onElegir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val densidad = LocalDensity.current
    Box(
        modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth().height(72.dp)) {
            val celda = maxWidth / items.size
            val centroDestino = if (seleccionada >= 0) celda * seleccionada + celda / 2 else (-200).dp
            val centro by animateDpAsState(centroDestino, label = "muesca")
            val forma = remember(centro, densidad, maxWidth) {
                FormaMuesca(
                    centro = with(densidad) { centro.toPx() },
                    profundidad = with(densidad) { 11.dp.toPx() },
                    medioAncho = with(densidad) { 24.dp.toPx() },
                    radio = with(densidad) { 28.dp.toPx() }
                )
            }

            // Fondo con la muesca (un poco transparente)
            Box(
                Modifier
                    .matchParentSize()
                    .shadow(14.dp, forma)
                    .clip(forma)
                    .background(colores.surface.copy(alpha = 0.9f))
                    .border(1.dp, colores.outlineVariant.copy(alpha = 0.6f), forma)
            )

            // Destinos: icono con su nombre debajo
            Row(Modifier.matchParentSize()) {
                items.forEachIndexed { i, item ->
                    val activa = i == seleccionada
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onElegir(i) }
                            .padding(top = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box {
                            Icon(
                                if (activa) item.iconoActivo else item.icono,
                                contentDescription = item.texto,
                                tint = if (activa) colores.primary else colores.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(26.dp)
                            )
                            if (item.avisos > 0) {
                                Box(
                                    Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 9.dp, y = (-5).dp)
                                        .defaultMinSize(minWidth = 17.dp, minHeight = 17.dp)
                                        .clip(CircleShape)
                                        .background(EstadoPedido.CANCELADO.color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (item.avisos > 9) "9+" else item.avisos.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 3.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            item.texto,
                            fontSize = 11.sp,
                            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
                            color = if (activa) colores.primary else colores.onSurface.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }
                }
            }

            // Punto de color apoyado en la muesca
            if (seleccionada >= 0) {
                val centroPunto = with(densidad) {
                    centro.toPx().coerceIn(28.dp.toPx() + 24.dp.toPx(), maxWidth.toPx() - 28.dp.toPx() - 24.dp.toPx()).toDp()
                }
                Box(
                    Modifier
                        .offset(x = centroPunto - 6.dp, y = 0.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colores.primary)
                )
            }
        }
    }
}

// Rectangulo redondeado con una muesca suave arriba, centrada en "centro". Si la muesca no cabe
// entre las esquinas se corre un poco hacia adentro
private class FormaMuesca(
    val centro: Float,
    val profundidad: Float,
    val medioAncho: Float,
    val radio: Float
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val w = size.width
        val h = size.height
        val r = radio
        val visible = centro > -medioAncho && centro < w + medioAncho
        val c = centro.coerceIn(r + medioAncho, w - r - medioAncho)
        val camino = Path().apply {
            moveTo(0f, r)
            arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
            if (visible) {
                lineTo(c - medioAncho, 0f)
                cubicTo(c - medioAncho * 0.45f, 0f, c - medioAncho * 0.6f, profundidad, c, profundidad)
                cubicTo(c + medioAncho * 0.6f, profundidad, c + medioAncho * 0.45f, 0f, c + medioAncho, 0f)
            }
            lineTo(w - r, 0f)
            arcTo(Rect(w - 2 * r, 0f, w, 2 * r), 270f, 90f, false)
            lineTo(w, h - r)
            arcTo(Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
            lineTo(r, h)
            arcTo(Rect(0f, h - 2 * r, 2 * r, h), 90f, 90f, false)
            close()
        }
        return Outline.Generic(camino)
    }
}
