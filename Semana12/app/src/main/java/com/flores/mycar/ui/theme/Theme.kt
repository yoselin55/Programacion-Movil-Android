package com.flores.mycar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.flores.mycar.domain.model.ModoTema

// Modo oscuro: fondo Smoky Black y superficies Olive Drab. El texto usa Bone y Floral White.
private val EsquemaOscuro = darkColorScheme(
    primary = Bone,
    onPrimary = SmokyBlack,
    primaryContainer = OliveDrab,
    onPrimaryContainer = FloralWhite,
    secondary = Bone,
    onSecondary = SmokyBlack,
    secondaryContainer = OliveDrab,
    onSecondaryContainer = FloralWhite,
    tertiary = FloralWhite,
    onTertiary = SmokyBlack,
    tertiaryContainer = lerp(SmokyBlack, OliveDrab, 0.6f),
    onTertiaryContainer = FloralWhite,
    background = SmokyBlack,
    onBackground = FloralWhite,
    surface = SmokyBlack,
    onSurface = FloralWhite,
    surfaceVariant = OliveDrab,
    onSurfaceVariant = Bone,
    surfaceTint = Bone,
    outline = lerp(OliveDrab, Bone, 0.35f),
    outlineVariant = OliveDrab,
    error = RojoAlertaClaro,
    onError = SmokyBlack,
    errorContainer = RojoAlerta,
    onErrorContainer = FloralWhite,
    surfaceContainerLowest = SmokyBlack,
    surfaceContainerLow = lerp(SmokyBlack, OliveDrab, 0.18f),
    surfaceContainer = lerp(SmokyBlack, OliveDrab, 0.30f),
    surfaceContainerHigh = lerp(SmokyBlack, OliveDrab, 0.45f),
    surfaceContainerHighest = lerp(SmokyBlack, OliveDrab, 0.62f)
)

// Modo claro: fondo Floral White y superficies Bone. El texto usa Smoky Black y Olive Drab.
private val EsquemaClaro = lightColorScheme(
    primary = OliveDrab,
    onPrimary = FloralWhite,
    primaryContainer = Bone,
    onPrimaryContainer = SmokyBlack,
    secondary = OliveDrab,
    onSecondary = FloralWhite,
    secondaryContainer = Bone,
    onSecondaryContainer = SmokyBlack,
    tertiary = SmokyBlack,
    onTertiary = FloralWhite,
    tertiaryContainer = lerp(Bone, FloralWhite, 0.5f),
    onTertiaryContainer = SmokyBlack,
    background = FloralWhite,
    onBackground = SmokyBlack,
    surface = FloralWhite,
    onSurface = SmokyBlack,
    surfaceVariant = Bone,
    onSurfaceVariant = OliveDrab,
    surfaceTint = OliveDrab,
    outline = OliveDrab,
    outlineVariant = Bone,
    error = RojoAlerta,
    onError = FloralWhite,
    errorContainer = RojoAlertaClaro,
    onErrorContainer = SmokyBlack,
    surfaceContainerLowest = FloralWhite,
    surfaceContainerLow = lerp(FloralWhite, Bone, 0.30f),
    surfaceContainer = lerp(FloralWhite, Bone, 0.55f),
    surfaceContainerHigh = lerp(FloralWhite, Bone, 0.80f),
    surfaceContainerHighest = Bone
)

/** Tema de Mycar. "modo" permite seguir al sistema o forzar claro u oscuro desde Perfil. */
@Composable
fun MycarTheme(
    modo: ModoTema = ModoTema.SISTEMA,
    content: @Composable () -> Unit
) {
    val oscuro = when (modo) {
        ModoTema.SISTEMA -> isSystemInDarkTheme()
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
    }

    // Los iconos de la barra de estado cambian de color segun el modo.
    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(ventana, vista).apply {
                isAppearanceLightStatusBars = !oscuro
                isAppearanceLightNavigationBars = !oscuro
            }
        }
    }

    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
