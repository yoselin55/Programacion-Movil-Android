package com.flores.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// Commit 7: la pantalla solo dibuja el estado; nunca llama a la API.
// Commit 9: fila de chips para filtrar por usuario y mensaje cuando no hay publicaciones.
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        FiltroUsuarios(filtro = filtro, onFiltrar = { vm.filtrar(it) })

        when (val e = estado) {
            is UiState.Cargando -> Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Cargando() }

            is UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) { ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.cargar() }) }

            is UiState.Exito -> if (e.datos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Sin publicaciones") }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(e.datos, key = { it.id }) { post ->
                        TarjetaPost(post, onClick = { onPostClick(post.id) })
                    }
                }
            }
        }
    }
}

// Fila desplazable: Todos y Usuario 1..10; el seleccionado se ve relleno.
@Composable
private fun FiltroUsuarios(filtro: Int?, onFiltrar: (Int?) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = filtro == null,
                onClick = { onFiltrar(null) },
                label = { Text("Todos") }
            )
        }
        items((1..10).toList()) { usuario ->
            FilterChip(
                selected = filtro == usuario,
                onClick = { onFiltrar(usuario) },
                label = { Text("Usuario $usuario") }
            )
        }
    }
}
