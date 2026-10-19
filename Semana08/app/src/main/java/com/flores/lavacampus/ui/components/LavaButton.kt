package com.flores.lavacampus.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Boton principal: toma el color primario del perfil activo
@Composable
fun LavaButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.height(52.dp)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}
