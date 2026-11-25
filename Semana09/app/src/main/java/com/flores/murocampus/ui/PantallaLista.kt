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
import com.flores.murocampus.ui.theme.FuenteTitulos
import com.flores.murocampus.ui.theme.RosaTexto
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// La pantalla solo dibuja el estado; nunca llama a la API.
// IA commit 3: portada y fotos del campus arriba; todo se desplaza en una sola lista.
// IA commit 4: busqueda por titulo arriba de todo.
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { BarraBusqueda(texto = busqueda, onCambio = { vm.buscar(it) }) }
        item { PortadaHero() }
        item { ExploraCampus() }
        item {
            Text(
                text = "Publicaciones de la comunidad",
                color = RosaTexto,
                fontFamily = FuenteTitulos,
                fontSize = 20.sp,
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
                item {
                    Text(
                        text = if (busqueda.isNotBlank()) "Sin resultados para “$busqueda”" else "Sin publicaciones",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
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
                label = { Text("Todos", fontSize = 16.sp) },
                colors = chipColores()
            )
        }
        items((1..10).toList()) { usuario ->
            FilterChip(
                selected = filtro == usuario,
                onClick = { onFiltrar(usuario) },
                label = { Text("Usuario $usuario", fontSize = 16.sp) },
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
