package com.flores.mycar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flores.mycar.domain.model.Auto

/** Fila compacta de un auto sin foto (modelo de la API): nombre, precio, favorito y carrito. */
@Composable
fun AutoFila(
    auto: Auto,
    esFavorito: Boolean,
    enCarrito: Boolean,
    onClick: () -> Unit,
    onFavorito: () -> Unit,
    onCarrito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FotoAuto("", Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(auto.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text(
                    precioEnSoles(auto.precio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavorito) {
                Icon(
                    imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onCarrito) {
                Icon(
                    imageVector = if (enCarrito) Icons.Filled.RemoveShoppingCart else Icons.Filled.AddShoppingCart,
                    contentDescription = if (enCarrito) "Quitar del carrito" else "Agregar al carrito"
                )
            }
        }
    }
}
