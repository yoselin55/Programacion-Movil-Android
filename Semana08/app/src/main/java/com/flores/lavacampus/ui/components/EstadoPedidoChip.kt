package com.flores.lavacampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (sobreColor) Color.White else estado.color.copy(alpha = 0.15f)
    ) {
        Text(
            estado.texto,
            color = estado.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
