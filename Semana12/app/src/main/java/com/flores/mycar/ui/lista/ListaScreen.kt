package com.flores.mycar.ui.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.BarraPrincipal
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.EstadoVacio
import com.flores.mycar.ui.components.ItemCard

/** Pantalla de lista de marcas: solo observa el estado y llama funciones del ViewModel. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onItemClick: (Int) -> Unit,
    bienvenida: String?,
    onBienvenidaMostrada: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val logos by viewModel.logos.collectAsStateWithLifecycle()

    // La bienvenida se muestra una sola vez, justo despues de iniciar sesion.
    val avisos = remember { SnackbarHostState() }
    LaunchedEffect(bienvenida) {
        if (bienvenida != null) {
            onBienvenidaMostrada()
            avisos.showSnackbar(bienvenida)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BarraPrincipal("Marcas", onMenu = onMenu) {
                // Permite volver a intentar la sincronizacion cuando regresa la conexion.
                IconButton(onClick = viewModel::refrescar) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                }
            }
        },
        snackbarHost = { SnackbarHost(avisos) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            aviso?.let {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
            when (val e = estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::refrescar)
                is UiState.Exito ->
                    if (e.datos.isEmpty()) EstadoVacio(Icons.Filled.Category, "No hay marcas para mostrar")
                    else ListaItems(e.datos, logos, onItemClick, onFavorito = viewModel::alternarFavorito, onPedirLogo = viewModel::pedirLogo)
            }
        }
    }
}

@Composable
private fun ListaItems(
    items: List<Item>,
    logos: Map<String, String>,
    onItemClick: (Int) -> Unit,
    onFavorito: (Int) -> Unit,
    onPedirLogo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item.id) },
                onFavorito = { onFavorito(item.id) },
                logoUrl = logos[item.titulo],
                onVisible = { onPedirLogo(item.titulo) }
            )
        }
    }
}
