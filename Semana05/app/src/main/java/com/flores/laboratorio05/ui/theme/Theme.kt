package com.flores.laboratorio05.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PortalAcademicoColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    secondary = HeaderGradientEnd,
    onSecondary = Color.White,
    background = ScreenBackground,
    onBackground = TextDark,
    surface = WhiteCard,
    onSurface = TextDark,
    surfaceVariant = CardGray,
    onSurfaceVariant = TextMuted,
    error = TextRed,
    onError = Color.White,
    errorContainer = SoftRedBg,
    onErrorContainer = TextRed
)

@Composable
fun PortalAcademicoTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PortalAcademicoColorScheme,
        typography = Typography,
        content = content
    )
}
