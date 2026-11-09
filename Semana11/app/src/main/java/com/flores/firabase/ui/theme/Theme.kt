package com.flores.firabase.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaOscuro = darkColorScheme(
    primary = RojoPrimarioOscuro,
    onPrimary = RojoSobrePrimarioOscuro,
    primaryContainer = RojoContenedorOscuro,
    onPrimaryContainer = RojoContenedor,
    background = FondoOscuro,
    surface = SuperficieOscura,
    surfaceVariant = CampoOscuro,
    onSurfaceVariant = TextoSecundarioOscuro,
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val EsquemaClaro = lightColorScheme(
    primary = RojoPrimario,
    onPrimary = Color.White,
    primaryContainer = RojoContenedor,
    onPrimaryContainer = RojoSobreContenedor,
    background = FondoClaro,
    surface = Color.White,
    surfaceVariant = CampoClaro,
    onSurfaceVariant = TextoSecundarioClaro,
    error = ErrorClaro,
    errorContainer = Color(0xFFFFE3D6),
    onErrorContainer = Color(0xFF5A1A12)
)

@Composable
fun FirabaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
