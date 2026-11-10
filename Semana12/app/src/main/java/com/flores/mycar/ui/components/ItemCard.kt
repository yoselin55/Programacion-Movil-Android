package com.flores.mycar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.flores.mycar.data.autos.MarcasEnVenta
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flores.mycar.domain.model.Item

/** Tarjeta de una marca: inicial en un circulo, nombre y estrella de favorito. */
@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onFavorito: () -> Unit,
    modifier: Modifier = Modifier,
    logoUrl: String? = null,
    onVisible: () -> Unit = {}
) {
    // Cuando la tarjeta aparece en pantalla se pide el logo de la marca.
    LaunchedEffect(item.titulo) { onVisible() }
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (logoUrl != null) {
                // Logo de la marca sobre fondo claro, para que se vea tambien en el modo oscuro.
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.size(76.dp)
                ) {
                    AsyncImage(
                        model = rememberModeloImagen(logoUrl),
                        contentDescription = "Logo de ${item.titulo}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = item.titulo.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(item.titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    if (MarcasEnVenta.enVenta(item.titulo)) "En venta" else item.subtitulo,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (MarcasEnVenta.enVenta(item.titulo)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavorito) {
                Icon(
                    imageVector = if (item.esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
