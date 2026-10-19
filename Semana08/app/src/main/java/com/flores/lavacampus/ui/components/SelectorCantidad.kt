package com.flores.lavacampus.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale

// Control con botones menos y mas. Solo avisa; las reglas de cantidad viven en el ViewModel
@Composable
fun SelectorCantidad(
    valor: Double,
    paso: Double,
    maximo: Double,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BotonCircular(Icons.Filled.Remove, "Menos", enabled = valor > 0, onClick = onMenos)
        Text(
            // 3.5 se muestra "3.5" y 5.0 se muestra "5"
            if (paso % 1.0 == 0.0 || valor == 0.0) valor.toInt().toString() else String.format(Locale.US, "%.1f", valor),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 36.dp)
        )
        BotonCircular(Icons.Filled.Add, "Mas", enabled = valor < maximo, onClick = onMas)
    }
}

@Composable
private fun BotonCircular(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(32.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    ) {
        Icon(icono, contentDescription = descripcion, modifier = Modifier.size(18.dp))
    }
}
