package com.flores.mycar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Colores que la persona puede elegir para su auto. */
val COLORES_AUTO = listOf(
    "Blanco", "Negro", "Gris", "Plata", "Rojo", "Azul",
    "Verde", "Amarillo", "Naranja", "Marrón", "Beige", "Dorado"
)

/** Color real que corresponde al nombre de un color de auto. */
fun colorDeNombre(nombre: String): Color = when (nombre.lowercase()) {
    "blanco" -> Color(0xFFF5F5F5)
    "negro" -> Color(0xFF1B1B1B)
    "gris" -> Color(0xFF8A8A8A)
    "plata", "plateado" -> Color(0xFFC0C0C0)
    "rojo" -> Color(0xFFC62828)
    "azul" -> Color(0xFF1E4DA8)
    "verde" -> Color(0xFF2E7D32)
    "amarillo" -> Color(0xFFF2C200)
    "naranja" -> Color(0xFFEF6C00)
    "marrón", "marron" -> Color(0xFF6D4C41)
    "beige" -> Color(0xFFD8CFBC)
    "dorado" -> Color(0xFFC9A227)
    else -> Color(0xFF9E9E9E)
}

/** Circulo pequeño con el color del auto. */
@Composable
fun PuntoColor(nombre: String, tamano: Dp = 14.dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(tamano)
            .clip(CircleShape)
            .background(colorDeNombre(nombre))
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
    )
}
