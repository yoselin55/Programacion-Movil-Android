package com.flores.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.murocampus.ui.theme.RosaTexto
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// Commit 7: la pantalla solo dibuja el estado; nunca llama a la API.
// Commit 9: chips para filtrar por usuario y mensaje cuando no hay publicaciones.
// IA commit 3: portada y fotos del campus arriba; todo se desplaza en una sola lista.
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { PortadaHero() }
        item { ExploraCampus() }
        item {
            Text(
                text = "Publicaciones de la comunidad",
                color = RosaTexto,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item { FiltroUsuarios(filtro = filtro, onFiltrar = { vm.filtrar(it) }) }

        when (val e = estado) {
            is UiState.Cargando -> item { Cargando(Modifier.padding(vertical = 8.dp)) }

            is UiState.Error -> item {
                ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.cargar() })
            }

            is UiState.Exito -> if (e.datos.isEmpty()) {
                item { Text("Sin publicaciones", modifier = Modifier.padding(16.dp)) }
            } else {
                items(e.datos, key = { it.id }) { post ->
                    TarjetaPost(post, onClick = { onPostClick(post.id) })
                }
            }
        }
    }
}

// Fila desplazable: Todos y Usuario 1..10; el seleccionado se ve relleno.
@Composable
private fun FiltroUsuarios(filtro: Int?, onFiltrar: (Int?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = filtro == null,
                onClick = { onFiltrar(null) },
                label = { Text("Todos") },
                colors = chipColores()
            )
        }
        items((1..10).toList()) { usuario ->
            FilterChip(
                selected = filtro == usuario,
                onClick = { onFiltrar(usuario) },
                label = { Text("Usuario $usuario") },
                colors = chipColores()
            )
        }
    }
}

// El chip seleccionado se ve relleno de color.
@Composable
private fun chipColores() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = Color.White
)
