package com.flores.mycar.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView

/** Detalle de una marca: muestra sus modelos (segundo endpoint de la API). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val titulo = (estado as? UiState.Exito)?.datos?.titulo ?: "Detalle"

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val e = estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::cargar)
                is UiState.Exito -> Contenido(e.datos)
            }
        }
    }
}

@Composable
private fun Contenido(item: Item) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(item.subtitulo, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Modelos (${item.modelos.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        if (item.modelos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Esta marca no tiene modelos registrados")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(item.modelos) { modelo ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(modelo, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
