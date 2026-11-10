package com.flores.mycar.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Banner con varias fotos que cambian solas con un fundido y un zoom lento (efecto de movimiento).
 * Se usa en el panel del administrador y en las promociones.
 */
@Composable
fun CarruselFotos(
    fotos: List<Int>,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    alto: Int = 190
) {
    var indice by remember { mutableIntStateOf(0) }
    val zoom = remember { Animatable(1f) }

    LaunchedEffect(fotos.size) {
        while (fotos.size > 1) {
            delay(4500)
            indice = (indice + 1) % fotos.size
        }
    }
    LaunchedEffect(indice) {
        zoom.snapTo(1f)
        zoom.animateTo(1.12f, tween(5200, easing = LinearEasing))
    }

    Box(modifier.fillMaxWidth().height(alto.dp).clip(RoundedCornerShape(24.dp))) {
        Crossfade(targetState = indice, animationSpec = tween(900), label = "carrusel") { i ->
            Image(
                painter = painterResource(fotos[i]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { scaleX = zoom.value; scaleY = zoom.value }
            )
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.3f to Color.Transparent, 1f to Color(0xCC11120D))))
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFFFFBF4))
            Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFFFBF4))
        }
        if (fotos.size > 1) {
            Row(Modifier.align(Alignment.BottomEnd).padding(18.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                fotos.indices.forEach { k ->
                    Box(Modifier.size(if (k == indice) 18.dp else 7.dp, 7.dp).clip(CircleShape).background(Color(0xFFFFFBF4).copy(alpha = if (k == indice) 1f else 0.5f)))
                }
            }
        }
    }
}
