package com.flores.campusgps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Mejora 3: esquema de colores único (claro) con la paleta de CampusGPS.
// Se quitaron el tema oscuro y los colores dinámicos para que la app siempre se vea igual.
private val EsquemaCampus = lightColorScheme(
    primary = Azul,
    onPrimary = Tinta,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Tinta,
    secondary = Tinta,
    onSecondary = Fondo,
    secondaryContainer = AzulSuave,
    onSecondaryContainer = Tinta,
    tertiary = Tinta,
    onTertiary = Fondo,
    background = Fondo,
    onBackground = Tinta,
    surface = Fondo,
    onSurface = Tinta,
    surfaceVariant = AzulSuave,
    onSurfaceVariant = TintaSuave,
    outline = Azul,
    outlineVariant = AzulClaro,
    surfaceContainerLowest = Fondo,
    surfaceContainerLow = AzulTenue,
    surfaceContainer = AzulTenue,
    surfaceContainerHigh = AzulSuave,
    surfaceContainerHighest = AzulSuave
)

@Composable
fun CampusGPSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaCampus,
        typography = Typography,
        content = content
    )
}
