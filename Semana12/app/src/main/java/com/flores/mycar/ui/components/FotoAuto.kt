package com.flores.mycar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/** Convierte el enlace de una foto propia (android.resource://...) en el id del recurso; los demas enlaces quedan igual. */
@Composable
fun rememberModeloImagen(url: String): Any {
    val contexto = LocalContext.current
    return remember(url) {
        if (url.startsWith("android.resource://")) {
            contexto.resources.getIdentifier(url.substringAfterLast('/'), "drawable", contexto.packageName)
        } else url
    }
}

/** Foto de un auto cargada desde un enlace. Si no hay enlace, muestra un fondo con un auto. */
@Composable
fun FotoAuto(url: String, modifier: Modifier = Modifier, escala: ContentScale = ContentScale.Crop) {
    if (url.isBlank()) {
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsCar,
                contentDescription = "Sin foto",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )
        }
    } else {
        AsyncImage(
            model = rememberModeloImagen(url),
            contentDescription = "Foto del auto",
            contentScale = escala,
            modifier = modifier
        )
    }
}
