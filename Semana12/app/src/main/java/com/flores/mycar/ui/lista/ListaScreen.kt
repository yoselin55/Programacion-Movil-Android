package com.flores.mycar.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.flores.mycar.ui.components.ItemCard

/** Pantalla de lista: solo observa el estado y llama funciones del ViewModel. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onItemClick: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Mycar") },
                actions = {
                    // Permite volver a intentar la sincronizacion cuando regresa la conexion.
                    TextButton(onClick = viewModel::refrescar) { Text("Actualizar") }
                    TextButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
                }
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            aviso?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            when (val e = estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::refrescar)
                is UiState.Exito ->
                    if (e.datos.isEmpty()) Vacio()
                    else ListaItems(e.datos, onItemClick, onFavorito = viewModel::alternarFavorito)
            }
        }
    }
}

@Composable
private fun ListaItems(
    items: List<Item>,
    onItemClick: (Int) -> Unit,
    onFavorito: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item.id) },
                onFavorito = { onFavorito(item.id) }
            )
        }
    }
}

@Composable
private fun Vacio() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay marcas para mostrar")
    }
}
