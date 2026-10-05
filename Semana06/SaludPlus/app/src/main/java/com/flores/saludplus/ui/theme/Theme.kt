package com.flores.saludplus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Relaciones:
// - Lo llama MainActivity
// - Usa los colores de Color.kt y la tipografía de Type.kt

// Esquema claro con la paleta azul de la clinica
private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    background = FondoApp,
    surface = Color.White,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario
)

@Composable
fun SaludplusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
