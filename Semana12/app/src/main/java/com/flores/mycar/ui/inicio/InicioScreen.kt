package com.flores.mycar.ui.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Search
import coil3.compose.AsyncImage
import com.flores.mycar.data.autos.MarcasEnVenta
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.R
import com.flores.mycar.domain.model.Promocion
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.autos.AutosViewModel
import com.flores.mycar.ui.components.AutoCard
import com.flores.mycar.ui.components.rememberModeloImagen
import com.flores.mycar.ui.components.BarraPrincipal
import com.flores.mycar.ui.theme.FloralWhite
import com.flores.mycar.ui.theme.SmokyBlack

/** Pantalla de inicio: una imagen de autos, los autos destacados y accesos rapidos. */
@Composable
fun InicioScreen(
    viewModel: AutosViewModel,
    sesion: Sesion,
    onMenu: () -> Unit,
    onAutos: () -> Unit,
    onMarcas: () -> Unit,
    onFavoritos: () -> Unit,
    onCarrito: () -> Unit,
    onAutoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val compras by viewModel.compras.collectAsStateWithLifecycle()
    val logos by viewModel.logos.collectAsStateWithLifecycle()
    val promociones by viewModel.promociones.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { viewModel.usuario(sesion.uid) }

    Scaffold(
        modifier = modifier,
        topBar = {
            BarraPrincipal("Mycar", onMenu = onMenu)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Portada(onAutos) }

            // Buscador (abre Autos) y marcas con su logo
            item {
                Surface(
                    onClick = onAutos,
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Buscar marca o auto",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
            item {
                Column {
                    Text(
                        "Marcas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                    )
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Solo las marcas que siguen en venta (el administrador puede desactivar alguna).
                        val visibles = (estado as? UiState.Exito)?.datos?.map { it.marca }
                        val marcasFila = MarcasEnVenta.principales
                            .filter { m -> visibles == null || visibles.any { it.equals(m, ignoreCase = true) } }
                            .take(4)
                        items(marcasFila) { marca ->
                            LaunchedEffect(marca) { viewModel.pedirLogo(marca) }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onAutos)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.size(68.dp)
                                ) {
                                    val logo = logos[marca]
                                    if (logo != null) {
                                        AsyncImage(
                                            model = rememberModeloImagen(logo),
                                            contentDescription = "Logo de $marca",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    } else {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(marca.first().uppercase(), style = MaterialTheme.typography.titleLarge, color = SmokyBlack)
                                        }
                                    }
                                }
                                Text(marca, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp), maxLines = 1)
                            }
                        }
                        // "Ver mas" al costado: abre la lista completa de marcas
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onMarcas)) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(64.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ver más marcas", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                                Text("Ver más", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp), maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Promociones activas del administrador
            if (promociones.isNotEmpty()) {
                item {
                    Column {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalOffer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Promociones", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 10.dp))
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(promociones, key = { _, p -> p.id }) { n, promo -> TarjetaPromocion(promo, n, onAutos) }
                        }
                    }
                }
            }

            // Autos destacados
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Autos destacados", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onAutos) { Text("Ver todos") }
                }
            }
            item {
                when (val e = estado) {
                    is UiState.Exito ->
                        if (e.datos.isEmpty()) {
                            Text(
                                "Aún no hay autos en venta. Cuando el administrador registre autos, aparecerán aquí.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                val elegidos = MarcasEnVenta.destacados.mapNotNull { (marca, modelo) ->
                                    e.datos.firstOrNull { it.marca.equals(marca, true) && it.modelo.equals(modelo, true) }
                                }.ifEmpty { e.datos.take(6) }
                                items(elegidos, key = { it.id }) { auto ->
                                    AutoCard(
                                        auto = auto,
                                        esFavorito = auto.id in compras.favoritos,
                                        enCarrito = auto.id in compras.carrito,
                                        onClick = { onAutoClick(auto.id) },
                                        onFavorito = { viewModel.alternarFavorito(auto.id) },
                                        onCarrito = { viewModel.alternarCarrito(auto.id) },
                                        modifier = Modifier.width(290.dp)
                                    )
                                }
                            }
                        }

                    is UiState.Error -> Text(
                        e.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    is UiState.Cargando -> LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                }
            }

        }
    }
}

/** Portada con la foto de los autos, el lema y el boton para ver los autos. */
@Composable
private fun Portada(onAutos: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(340.dp)) {
        Image(
            painter = painterResource(R.drawable.portada_inicio),
            contentDescription = "Autos en exhibición",
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(0f, 0.35f),
            modifier = Modifier.fillMaxSize()
        )
        // Degradado oscuro abajo para que se lea el texto.
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.35f to Color.Transparent, 1f to SmokyBlack.copy(alpha = 0.88f))
            )
        )
        Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Text(
                "Encuentra tu auto ideal",
                style = MaterialTheme.typography.headlineSmall,
                color = FloralWhite
            )
            Text(
                "Mira fotos, compara precios y agenda una cita para verlo.",
                style = MaterialTheme.typography.bodyMedium,
                color = FloralWhite.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )
            Button(
                onClick = onAutos,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FloralWhite, contentColor = SmokyBlack)
            ) {
                Text("Ver autos")
            }
        }
    }
}

/** Tarjeta de una promocion: foto de fondo, el descuento en grande, el nombre y el mes. */
@Composable
private fun TarjetaPromocion(promo: Promocion, indice: Int, onClick: () -> Unit) {
    val foto = if (indice % 2 == 0) com.flores.mycar.R.drawable.promo_bmw else com.flores.mycar.R.drawable.promo_evoque
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.width(290.dp).height(170.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(foto),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.horizontalGradient(0f to androidx.compose.ui.graphics.Color(0xDD11120D), 1f to androidx.compose.ui.graphics.Color(0x2211120D))))
            Column(Modifier.align(Alignment.CenterStart).padding(18.dp)) {
                Text("-${promo.descuento}%", style = MaterialTheme.typography.displaySmall, color = androidx.compose.ui.graphics.Color(0xFFD8CFBC))
                Text(promo.titulo, style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color(0xFFFFFBF4), maxLines = 2)
                Text(
                    "${promo.mes} · ${promo.marca.ifBlank { "Todas las marcas" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color(0xFFFFFBF4).copy(alpha = 0.85f),
                    maxLines = 2
                )
            }
        }
    }
}
