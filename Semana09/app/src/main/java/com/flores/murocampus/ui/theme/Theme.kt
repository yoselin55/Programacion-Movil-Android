package com.flores.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulMuroCampus,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    error = RojoError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AzulMuroCampus,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulMuroCampus,
    secondary = AzulMuroCampus,
    onSecondary = Color.White,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    error = RojoError,
    onError = Color.White
)

// Commit 1: tema MuroCampus sin colores dinámicos para conservar la paleta del PDF.
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
