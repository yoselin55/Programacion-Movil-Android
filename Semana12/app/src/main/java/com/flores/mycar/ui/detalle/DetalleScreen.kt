package com.flores.mycar.ui.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.precioEnSoles
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.EstadoVacio
import com.flores.mycar.ui.components.IconoAtras

/** Detalle de una marca: sus autos en venta (hasta 8, los modelos del año que da la API; con foto si la hay). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    onVerAuto: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val enVenta by viewModel.enVenta.collectAsStateWithLifecycle()
    val titulo = (estado as? UiState.Exito)?.datos?.titulo ?: "Detalle"

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = { IconoAtras(onVolver) },
                actions = {
                    (estado as? UiState.Exito)?.let { exito ->
                        IconButton(onClick = viewModel::alternarFavorito) {
                            Icon(
                                imageVector = if (exito.datos.esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (exito.datos.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val e = estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::cargar)
                is UiState.Exito -> Contenido(
                    item = e.datos,
                    enVenta = enVenta,
                    onVerAuto = onVerAuto
                )
            }
        }
    }
}

@Composable
private fun Contenido(
    item: Item,
    enVenta: List<Auto>,
    onVerAuto: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(item.subtitulo, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Autos de esta marca que estan en venta
        item {
            Text(
                "Autos en venta (${enVenta.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (enVenta.isEmpty()) {
            item {
                Text(
                    "Cargando los autos de ${item.titulo}...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(enVenta, key = { it.id }) { auto ->
            Card(
                onClick = { onVerAuto(auto.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    FotoAuto(auto.imagenUrl, Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)))
                    Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                        Text(auto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            precioEnSoles(auto.precio),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("Ver detalle", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

    }
}
