package com.flores.lavacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.flores.lavacampus.model.Rol

// Estudiante: turquesa. Administrador: violeta
private val ClienteClaro = lightColorScheme(
    primary = Color(0xFF0891B2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),
    secondaryContainer = Color(0xFFCFFAFE),
    onPrimaryContainer = Color(0xFF164E63),
    background = Color(0xFFF0FCFF),
    surface = Color.White
)

private val ClienteOscuro = darkColorScheme(
    primary = Color(0xFF67E8F9),
    onPrimary = Color(0xFF083344),
    primaryContainer = Color(0xFF155E75),
    secondaryContainer = Color(0xFF155E75),
    onPrimaryContainer = Color(0xFFCFFAFE),
    background = Color(0xFF0B1A1F),
    surface = Color(0xFF10252C)
)

private val AdminClaro = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    secondaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    background = Color(0xFFFAF8FF),
    surface = Color.White
)

private val AdminOscuro = darkColorScheme(
    primary = Color(0xFFC4B5FD),
    onPrimary = Color(0xFF2E1065),
    primaryContainer = Color(0xFF5B21B6),
    secondaryContainer = Color(0xFF5B21B6),
    onPrimaryContainer = Color(0xFFEDE9FE),
    background = Color(0xFF17121F),
    surface = Color(0xFF1F1829)
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
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
