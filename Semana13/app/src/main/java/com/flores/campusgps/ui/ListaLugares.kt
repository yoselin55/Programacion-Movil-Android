package com.flores.campusgps.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.campusgps.model.Lugar
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.ui.theme.Azul
import com.flores.campusgps.ui.theme.AzulSuave
import com.flores.campusgps.ui.theme.Tinta
import com.flores.campusgps.util.formatearDistancia
import com.flores.campusgps.util.formatearTiempoAPie

// Commit 7: lista de lugares con nombre, categoría y distancia; el lugar seleccionado se resalta.
// No conoce el ViewModel: recibe la lista, el id seleccionado y los eventos.
// Mejora 3: filas más limpias (como los resultados de Google Maps): ícono de la categoría, nombre y una sola
// línea de detalle con la categoría, la distancia y el tiempo a pie. La fila elegida muestra Cómo llegar.
@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    masCercanoId: Int? = null,
    onComoLlegar: (Lugar) -> Unit = {}
) {
    // Mejora 3: cuando se elige un lugar (o se recupera el último visitado), la lista baja hasta su fila.
    val estadoLista = rememberLazyListState()
    val orden = lugares.map { it.lugar.id }
    LaunchedEffect(seleccionado, orden) {
        val posicion = orden.indexOf(seleccionado)
        if (posicion >= 0) estadoLista.animateScrollToItem(posicion)
    }

    LazyColumn(
        state = estadoLista,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(lugares, key = { it.lugar.id }) { item ->
            val esSeleccionado = item.lugar.id == seleccionado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    // Commit 7: la fila seleccionada tiene otro fondo.
                    .background(if (esSeleccionado) AzulSuave else Color.Transparent)
                    .clickable { onClick(item.lugar.id) }
                    .animateContentSize()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ícono de la categoría dentro de un círculo azul.
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(Azul),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconoDeCategoria(item.lugar.categoria),
                            contentDescription = item.lugar.categoria,
                            tint = Tinta
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.lugar.nombre, fontWeight = FontWeight.SemiBold)
                        // Commit 7: si todavía no hay ubicación (metros nulo) se muestra "Sin calcular".
                        val detalle = item.metros
                            ?.let { "${item.lugar.categoria} · ${formatearDistancia(it)} · ${formatearTiempoAPie(it)}" }
                            ?: "${item.lugar.categoria} · Sin calcular"
                        Text(
                            detalle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (item.lugar.id == masCercanoId) {
                        // Etiqueta que hace juego con el marcador verde del mapa.
                        Text(
                            "Más cercano",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mejora 3: solo la fila seleccionada muestra el botón para abrir las indicaciones.
                if (esSeleccionado) {
                    Button(
                        onClick = { onComoLlegar(item.lugar) },
                        modifier = Modifier.padding(top = 10.dp, start = 54.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Cómo llegar")
                    }
                }
            }
        }
    }
}
