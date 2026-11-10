package com.flores.mycar.ui.autos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.MarcaVenta
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.components.AutoCard
import com.flores.mycar.ui.components.AutoFila
import com.flores.mycar.ui.components.BarraPrincipal
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.EstadoVacio

/**
 * Autos en venta: cada marca de la API con sus autos (los modelos de la API). Los autos con foto
 * salen primero; los demas son filas sencillas. Tiene buscador por marca o por auto.
 */
@Composable
fun AutosScreen(
    viewModel: AutosViewModel,
    sesion: Sesion,
    onMenu: () -> Unit,
    onAutoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.secciones.collectAsStateWithLifecycle()
    val compras by viewModel.compras.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { viewModel.usuario(sesion.uid) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    var marcaElegida by rememberSaveable { mutableStateOf("") }
    // Al buscar se piden los autos de todas las marcas, para poder encontrar un auto por su nombre.
    LaunchedEffect(busqueda.trim().length >= 2) { if (busqueda.trim().length >= 2) viewModel.cargarTodas() }

    Scaffold(
        modifier = modifier,
        topBar = { BarraPrincipal("Autos en venta", onMenu = onMenu) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar marca o auto (ej. Porsche)") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filtro rapido por marca
            val marcasEnVenta = (estado as? UiState.Exito)?.datos.orEmpty().map { it.nombre }
            if (marcasEnVenta.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    item { FilterChip(selected = marcaElegida.isEmpty(), onClick = { marcaElegida = "" }, label = { Text("Todas") }) }
                    items(marcasEnVenta) { nombre ->
                        FilterChip(selected = marcaElegida == nombre, onClick = { marcaElegida = nombre }, label = { Text(nombre) })
                    }
                }
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (val e = estado) {
                    is UiState.Cargando -> Cargando()
                    is UiState.Error -> ErrorView(e.mensaje, onReintentar = viewModel::observar)
                    is UiState.Exito -> {
                        val q = busqueda.trim()
                        val porMarca = if (marcaElegida.isEmpty()) e.datos else e.datos.filter { it.nombre == marcaElegida }
                        val secciones = if (q.isEmpty()) porMarca else porMarca.mapNotNull { filtrar(it, q) }
                        when {
                            e.datos.isEmpty() -> EstadoVacio(
                                Icons.Filled.DirectionsCar,
                                "Aún no hay autos en venta",
                                "Conéctate a internet para traer el catálogo de marcas."
                            )

                            secciones.isEmpty() -> EstadoVacio(
                                Icons.Filled.SearchOff,
                                "No encontrado",
                                "No hay autos que coincidan con «$q». Prueba con otro nombre."
                            )

                            else -> LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                secciones.forEach { s ->
                                    item(key = "marca_${s.id}") {
                                        // Cuando la marca aparece en pantalla se piden sus autos a la API.
                                        LaunchedEffect(s.id) { viewModel.cargarMarca(s.id) }
                                        Column(Modifier.padding(top = 10.dp)) {
                                            Text(s.nombre, style = MaterialTheme.typography.titleLarge)
                                            Text(
                                                when {
                                                    !s.cargada && s.autos.isEmpty() -> "Cargando autos..."
                                                    s.autos.isEmpty() -> "Sin autos registrados"
                                                    else -> "${s.autos.size} autos"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    items(s.autos, key = { it.id }) { auto ->
                                        if (auto.imagenUrl.isNotBlank()) {
                                            AutoCard(
                                                auto = auto,
                                                esFavorito = auto.id in compras.favoritos,
                                                enCarrito = auto.id in compras.carrito,
                                                onClick = { onAutoClick(auto.id) },
                                                onFavorito = { viewModel.alternarFavorito(auto.id) },
                                                onCarrito = { viewModel.alternarCarrito(auto.id) },
                                                modifier = Modifier.fillMaxWidth().animateItem()
                                            )
                                        } else {
                                            AutoFila(
                                                auto = auto,
                                                esFavorito = auto.id in compras.favoritos,
                                                enCarrito = auto.id in compras.carrito,
                                                onClick = { onAutoClick(auto.id) },
                                                onFavorito = { viewModel.alternarFavorito(auto.id) },
                                                onCarrito = { viewModel.alternarCarrito(auto.id) },
                                                modifier = Modifier.fillMaxWidth().animateItem()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Deja solo lo que coincide con la busqueda: la marca completa, o solo los autos que coinciden. */
private fun filtrar(marca: MarcaVenta, q: String): MarcaVenta? {
    if (marca.nombre.contains(q, ignoreCase = true)) return marca
    val autos = marca.autos.filter { it.nombre.contains(q, ignoreCase = true) }
    return if (autos.isEmpty()) null else marca.copy(autos = autos)
}
