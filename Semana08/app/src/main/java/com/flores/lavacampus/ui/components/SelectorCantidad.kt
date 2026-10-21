package com.flores.lavacampus.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// Control con botones menos y mas. Solo avisa; las reglas de cantidad viven en el ViewModel
// sufijo: unidad que se muestra junto al valor (ej. "kg")
@Composable
fun SelectorCantidad(
    valor: Double,
    paso: Double,
    maximo: Double,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    modifier: Modifier = Modifier,
    sufijo: String = ""
) {
    val entero = paso % 1.0 == 0.0 || valor == 0.0
    val numero = if (entero) valor.toInt().toString() else String.format(Locale.US, "%.1f", valor)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BotonCircular(Icons.Filled.Remove, "Menos", enabled = valor > 0, onClick = onMenos)
        Text(
            if (valor > 0 && sufijo.isNotEmpty()) "$numero $sufijo" else numero,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 56.dp)
        )
        BotonCircular(Icons.Filled.Add, "Mas", enabled = valor < maximo, onClick = onMas)
    }
}

@Composable
private fun BotonCircular(icono: ImageVector, descripcion: String, enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(30.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    ) {
        Icon(icono, contentDescription = descripcion, modifier = Modifier.size(16.dp))
    }
}
