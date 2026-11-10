package com.flores.mycar.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.flores.mycar.domain.model.EstadoAuto

/** "S/ 85,000" */
fun precioEnSoles(precio: Double): String = "S/ " + "%,.0f".format(precio)

/** Etiqueta con el estado del auto. El disponible destaca; los demas se ven mas tenues. */
@Composable
fun EtiquetaEstado(estado: EstadoAuto, modifier: Modifier = Modifier) {
    val disponible = estado == EstadoAuto.DISPONIBLE
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = estado.etiqueta,
            style = MaterialTheme.typography.labelLarge,
            color = if (disponible) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * Tarjeta de un auto en venta: foto con estrella de favorito, nombre, año, color, precio, estado,
 * el boton "Ver detalle" y el boton del carrito.
 */
@Composable
fun AutoCard(
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            Box {
                FotoAuto(
                    auto.imagenUrl,
                    Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).background(Color.White),
                    ContentScale.Fit
                )
                // Etiqueta de descuento sobre la foto.
                if (auto.descuento > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                    ) {
                        Text(
                            "-${auto.descuento}%",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                // Estrella de favorito sobre la foto.
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.78f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                ) {
                    IconButton(onClick = onFavorito, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(auto.marca.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Text(auto.modelo.ifBlank { auto.marca }, style = MaterialTheme.typography.titleLarge, maxLines = 2)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrecioConDescuento(auto, MaterialTheme.typography.titleMedium)
                    EtiquetaEstado(auto.estado)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver detalle del auto")
                    }
                    if (auto.estado == EstadoAuto.DISPONIBLE) {
                        FilledTonalIconButton(onClick = onCarrito) {
                            Icon(
                                imageVector = if (enCarrito) Icons.Filled.RemoveShoppingCart else Icons.Filled.AddShoppingCart,
                                contentDescription = if (enCarrito) "Quitar del carrito" else "Agregar al carrito"
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Precio del auto; si tiene promocion, muestra tambien el precio anterior tachado. */
@Composable
fun PrecioConDescuento(auto: Auto, estilo: androidx.compose.ui.text.TextStyle, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(precioEnSoles(auto.precio), style = estilo, color = if (auto.descuento > 0) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Unspecified)
        if (auto.descuento > 0) {
            Text(
                precioEnSoles(auto.precioOriginal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
