package com.flores.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Commit 10 (rediseño): un solo tema claro con la paleta de la app
private val EsquemaColores = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = Superficie,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = Amarillo,
    onSecondary = Tinta,
    background = Crema,
    onBackground = Tinta,
    surface = Superficie,
    onSurface = Tinta,
    surfaceVariant = VerdeClaro,
    onSurfaceVariant = TintaSuave,
    outline = VerdeMedio,
    error = Rojo,
    onError = Superficie,
    errorContainer = RojoClaro,
    onErrorContainer = Rojo
)

@Composable
fun CentroTareasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaColores,
        typography = Typography,
        content = content
    )
}
