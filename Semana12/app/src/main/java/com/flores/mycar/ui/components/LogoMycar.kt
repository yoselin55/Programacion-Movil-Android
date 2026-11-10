package com.flores.mycar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flores.mycar.R
import com.flores.mycar.ui.theme.FloralWhite

/**
 * Logo de Mycar tal como es, sin cuadro. Tiene el fondo transparente.
 * En el modo oscuro lleva un resplandor claro detras, para que las letras negras se lean.
 */
@Composable
fun LogoMycar(
    ancho: Dp,
    modifier: Modifier = Modifier
) {
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Box(modifier = modifier.width(ancho), contentAlignment = Alignment.Center) {
        if (oscuro) {
            // Resplandor claro en forma de elipse, detras de todo el logo.
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .drawBehind {
                        val radio = size.width * 0.62f
                        withTransform({ scale(1f, size.height / (radio * 2f) * 1.25f, pivot = center) }) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    0f to FloralWhite.copy(alpha = 0.92f),
                                    0.6f to FloralWhite.copy(alpha = 0.55f),
                                    1f to FloralWhite.copy(alpha = 0f),
                                    center = center,
                                    radius = radio
                                ),
                                radius = radio,
                                center = center
                            )
                        }
                    }
            )
        }
        Image(
            painter = painterResource(R.drawable.logo_mycar),
            contentDescription = "Logo de Mycar",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
