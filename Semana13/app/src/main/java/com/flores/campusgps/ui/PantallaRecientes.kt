package com.flores.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.campusgps.ui.theme.Azul
import com.flores.campusgps.ui.theme.AzulSuave
import com.flores.campusgps.ui.theme.Tinta
import com.flores.campusgps.ui.theme.TintaSuave
import com.flores.campusgps.viewmodel.MapaViewModel

// Mejora 3 (recientes): muestra los últimos lugares visitados y las últimas búsquedas.
// Tocar uno lleva de vuelta al mapa con ese lugar elegido o esa búsqueda aplicada.
@Composable
fun PantallaRecientes(
    vm: MapaViewModel,
    onVerEnMapa: (Int) -> Unit,
    onBuscar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val recientes by vm.recientes.collectAsState()
    val busquedas by vm.busquedasRecientes.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { TituloSeccion("Últimos lugares visitados", Icons.Filled.Place) }

        if (recientes.isEmpty()) {
            item {
                EstadoVacio(
                    "Todavía no has visitado lugares",
                    "Elige un lugar en el mapa y aparecerá aquí para volver a verlo rápido."
                )
            }
        } else {
            items(recientes, key = { "lugar${it.id}" }) { lugar ->
                Card(
                    onClick = { onVerEnMapa(lugar.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(Azul),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconoDeCategoria(lugar.categoria), contentDescription = null, tint = Tinta)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(lugar.nombre, fontWeight = FontWeight.Bold)
                            Text(
                                lugar.categoria,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("Ver en el mapa", style = MaterialTheme.typography.labelMedium, color = TintaSuave)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TintaSuave)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            TituloSeccion("Búsquedas recientes", Icons.Filled.History)
        }

        if (busquedas.isEmpty()) {
            item {
                EstadoVacio(
                    "Todavía no has buscado nada",
                    "Usa la barra de búsqueda del mapa y tus búsquedas se guardarán aquí."
                )
            }
        } else {
            items(busquedas, key = { "busqueda$it" }) { texto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AzulSuave)
                        .clickable { onBuscar(texto) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = TintaSuave)
                    Spacer(Modifier.width(12.dp))
                    Text(texto, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TintaSuave)
                }
            }
        }

        // Si hay algo guardado, se puede borrar todo desde aquí.
        if (recientes.isNotEmpty() || busquedas.isNotEmpty()) {
            item {
                TextButton(onClick = { vm.borrarHistorial() }) {
                    Text("Borrar historial", color = Tinta)
                }
            }
        }
    }
}

// Título de una sección con un ícono pequeño.
@Composable
private fun TituloSeccion(texto: String, icono: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = Tinta)
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

// Mensaje cuando una lista todavía está vacía.
@Composable
private fun EstadoVacio(titulo: String, detalle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AzulSuave)
            .padding(16.dp)
    ) {
        Text(titulo, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
