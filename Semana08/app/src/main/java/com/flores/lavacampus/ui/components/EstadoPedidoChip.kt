package com.flores.lavacampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido

// Etiqueta pequena con el color del estado
@Composable
fun EstadoPedidoChip(
    estado: EstadoPedido,
    modifier: Modifier = Modifier,
    sobreColor: Boolean = false // true: fondo blanco, para tarjetas de color
) {
    // En modo oscuro el texto se aclara para que el azul y el verde se lean sobre el fondo
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val texto = if (oscuro && !sobreColor) lerp(estado.color, Color.White, 0.4f) else estado.color
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (sobreColor) Color.White else estado.color.copy(alpha = if (oscuro) 0.22f else 0.15f)
    ) {
        Text(
            estado.texto,
            color = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
