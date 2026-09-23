package com.flores.clinicasalud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlueCard,
    onPrimaryContainer = AccentBlue,
    secondaryContainer = LightBlueCard,
    onSecondaryContainer = AccentBlue,
    background = BackgroundWhite,
    onBackground = TextDark,
    surface = BackgroundWhite,
    onSurface = TextDark
)

@Composable
fun ClinicaSaludTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
