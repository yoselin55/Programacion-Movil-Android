package com.flores.mycar.ui.favoritos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.autos.AutosViewModel
import com.flores.mycar.ui.components.AutoCard
import com.flores.mycar.ui.components.BarraPrincipal
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.ErrorView
import com.flores.mycar.ui.components.EstadoVacio
import com.flores.mycar.ui.components.ItemCard

/** Favoritos: dos pestañas, una con los autos favoritos y otra con las marcas favoritas. */
@Composable
fun FavoritosScreen(
    autosVm: AutosViewModel,
    marcasVm: FavoritosViewModel,
    sesion: Sesion,
    onMenu: () -> Unit,
    onAutoClick: (String) -> Unit,
    onMarcaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val autos by autosVm.estado.collectAsStateWithLifecycle()
    val compras by autosVm.compras.collectAsStateWithLifecycle()
    val marcas by marcasVm.estado.collectAsStateWithLifecycle()
    val logos by marcasVm.logos.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { autosVm.usuario(sesion.uid) }
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = { BarraPrincipal("Favoritos", onMenu = onMenu) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = pestana) {
                Tab(selected = pestana == 0, onClick = { pestana = 0 }, text = { Text("Autos") })
                Tab(selected = pestana == 1, onClick = { pestana = 1 }, text = { Text("Marcas") })
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (pestana == 0) {
                    when (val e = autos) {
                        is UiState.Cargando -> Cargando()
                        is UiState.Error -> ErrorView(e.mensaje, onReintentar = autosVm::observar)
                        is UiState.Exito -> {
                            val favoritos = e.datos.filter { it.id in compras.favoritos }
                            if (favoritos.isEmpty()) {
                                EstadoVacio(
                                    Icons.Filled.DirectionsCar,
                                    "Aún no tienes autos favoritos",
                                    "Toca la estrella de un auto para guardarlo aquí."
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(favoritos, key = { it.id }) { auto ->
                                        AutoCard(
                                            auto = auto,
                                            esFavorito = true,
                                            enCarrito = auto.id in compras.carrito,
                                            onClick = { onAutoClick(auto.id) },
                                            onFavorito = { autosVm.alternarFavorito(auto.id) },
                                            onCarrito = { autosVm.alternarCarrito(auto.id) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    when (val e = marcas) {
                        is UiState.Cargando -> Cargando()
                        is UiState.Error -> ErrorView(e.mensaje, onReintentar = {})
                        is UiState.Exito ->
                            if (e.datos.isEmpty()) {
                                EstadoVacio(
                                    Icons.Outlined.StarBorder,
                                    "Aún no tienes marcas favoritas",
                                    "Toca la estrella de una marca para guardarla aquí."
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(e.datos, key = { it.id }) { item ->
                                        ItemCard(
                                            item = item,
                                            onClick = { onMarcaClick(item.id) },
                                            onFavorito = { marcasVm.alternarFavorito(item.id) },
                                            logoUrl = logos[item.titulo],
                                            onVisible = { marcasVm.pedirLogo(item.titulo) }
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
