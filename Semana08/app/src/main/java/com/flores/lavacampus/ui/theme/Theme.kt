package com.flores.lavacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.flores.lavacampus.model.Rol

// Letras un 15 % mas grandes en toda la app (se suma al tamano de letra del sistema)
private const val ESCALA_LETRA = 1.15f

// ---------- Estudiante: paleta azul ----------
// #021024  #052659  #5483B3  #7DA0CA  #C1E8FF
private val ClienteClaro = lightColorScheme(
    primary = Color(0xFF052659),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC1E8FF),
    onPrimaryContainer = Color(0xFF052659),
    secondary = Color(0xFF5483B3),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC1E8FF),
    onSecondaryContainer = Color(0xFF052659),
    tertiary = Color(0xFF7DA0CA),
    background = Color(0xFFF0F9FF),
    onBackground = Color(0xFF021024),
    surface = Color.White,
    onSurface = Color(0xFF021024),
    surfaceVariant = Color(0xFFE8F3FB),
    outlineVariant = Color(0xFFD3E4F5)
)

private val ClienteOscuro = darkColorScheme(
    primary = Color(0xFFC1E8FF),
    onPrimary = Color(0xFF021024),
    primaryContainer = Color(0xFF052659),
    onPrimaryContainer = Color(0xFFC1E8FF),
    secondary = Color(0xFF7DA0CA),
    onSecondary = Color(0xFF021024),
    secondaryContainer = Color(0xFF052659),
    onSecondaryContainer = Color(0xFFC1E8FF),
    tertiary = Color(0xFF5483B3),
    background = Color(0xFF021024),
    onBackground = Color(0xFFE8F5FF),
    surface = Color(0xFF0A2147),
    onSurface = Color(0xFFE8F5FF),
    surfaceVariant = Color(0xFF123365),
    outlineVariant = Color(0xFF2F5A92)
)

// ---------- Administrador: paleta vino y rosa ----------
// #4B0F1E  #6D1D32  #8E2B44  #B23C59  #CC5671  #E07A94  #F7D6DC
private val AdminClaro = lightColorScheme(
    primary = Color(0xFF8E2B44),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF7D6DC),
    onPrimaryContainer = Color(0xFF4B0F1E),
    secondary = Color(0xFFB23C59),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF7D6DC),
    onSecondaryContainer = Color(0xFF4B0F1E),
    tertiary = Color(0xFFCC5671),
    background = Color(0xFFFDF5F6),
    onBackground = Color(0xFF4B0F1E),
    surface = Color.White,
    onSurface = Color(0xFF4B0F1E),
    surfaceVariant = Color(0xFFF7E9EC),
    outlineVariant = Color(0xFFEBD3D8)
)

private val AdminOscuro = darkColorScheme(
    primary = Color(0xFFE07A94),
    onPrimary = Color(0xFF4B0F1E),
    primaryContainer = Color(0xFF8E2B44),
    onPrimaryContainer = Color(0xFFF7D6DC),
    secondary = Color(0xFFCC5671),
    onSecondary = Color(0xFF4B0F1E),
    secondaryContainer = Color(0xFF6D1D32),
    onSecondaryContainer = Color(0xFFF7D6DC),
    tertiary = Color(0xFFB23C59),
    background = Color(0xFF2A0912),
    onBackground = Color(0xFFF7D6DC),
    surface = Color(0xFF3F1220),
    onSurface = Color(0xFFF7D6DC),
    surfaceVariant = Color(0xFF5A1A2D),
    outlineVariant = Color(0xFF7A2F45)
)

// En las pantallas se usa MaterialTheme.colorScheme.primary, nunca un color fijo
@Composable
fun LavaTheme(
    rol: Rol = Rol.CLIENTE,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        rol == Rol.ADMIN && darkTheme -> AdminOscuro
        rol == Rol.ADMIN -> AdminClaro
        darkTheme -> ClienteOscuro
        else -> ClienteClaro
    }
    val densidad = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(densidad.density, densidad.fontScale * ESCALA_LETRA)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
