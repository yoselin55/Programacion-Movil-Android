package com.flores.mycar.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flores.mycar.domain.model.Item

/** Tarjeta de una marca con su estrella de favorito. Solo dibuja y avisa los clics. */
@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                Text(item.subtitulo, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onFavorito) {
                Text(
                    text = if (item.esFavorito) "★" else "☆",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
