package com.flores.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RosaOscuro,
    onPrimary = Color.White,
    primaryContainer = RosaPastel,
    background = FondoSuave,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    error = RojoError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RosaOscuro,
    onPrimary = Color.White,
    primaryContainer = RosaPastel,
    onPrimaryContainer = RosaTexto,
    secondary = Rosa,
    onSecondary = Color.White,
    background = FondoSuave,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    error = RojoError,
    onError = Color.White
)

// Tema MuroCampus sin colores dinamicos para conservar la paleta propia.
@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
