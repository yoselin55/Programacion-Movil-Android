package com.flores.mycar.ui.carrito

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.components.BarraPrincipal
import com.flores.mycar.ui.components.DialogoCompra
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.precioEnSoles
import com.flores.mycar.ui.theme.FloralWhite
import com.flores.mycar.ui.theme.SmokyBlack

/** Carrito de compras: los autos guardados, el resumen con el total y el boton para comprar (pago de prueba). */
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    sesion: Sesion,
    onMenu: () -> Unit,
    onVerAutos: () -> Unit,
    onAutoClick: (String) -> Unit,
    onComprado: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { viewModel.usuario(sesion.uid) }
    var confirmar by rememberSaveable { mutableStateOf(false) }

    if (confirmar) {
        // La compra se arma en una pantalla completa (no en una ventana emergente).
        DialogoCompra(
            resumen = if (estado.items.size == 1) estado.items.first().nombre else "${estado.items.size} autos",
            total = precioEnSoles(estado.total),
            onConfirmar = { recojo, direccion -> confirmar = false; viewModel.comprar(sesion, recojo, direccion); onComprado() },
            onCancelar = { confirmar = false }
        )
        return
    }

    Scaffold(
        modifier = modifier,
        topBar = { BarraPrincipal("Carrito de compras", onMenu = onMenu) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            estado.mensaje?.let {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(it, modifier = Modifier.weight(1f).padding(start = 12.dp))
                        IconButton(onClick = viewModel::mensajeMostrado) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar aviso")
                        }
                    }
                }
            }

            if (estado.items.isEmpty()) {
                CarritoVacio(onVerAutos, Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(Modifier.padding(bottom = 4.dp)) {
                            Text("Tu selección", style = MaterialTheme.typography.headlineSmall)
                            Text(
                                if (estado.items.size == 1) "1 auto listo para comprar" else "${estado.items.size} autos listos para comprar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(estado.items, key = { it.id }) { auto ->
                        AutoEnCarrito(
                            auto = auto,
                            onClick = { onAutoClick(auto.id) },
                            onQuitar = { viewModel.quitar(auto.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }

                // Resumen y compra
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    tonalElevation = 3.dp
                ) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp)) {
                        FilaResumen("Autos (${estado.items.size})", precioEnSoles(estado.total))
                        FilaResumen("Envío", "Gratis")
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", style = MaterialTheme.typography.titleLarge)
                            Text(precioEnSoles(estado.total), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Button(
                            onClick = { confirmar = true },
                            enabled = estado.total > 0 && !estado.comprando,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 14.dp).height(54.dp)
                        ) {
                            Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text(if (estado.comprando) "Comprando..." else "Comprar ahora", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

/** Carrito vacio: una foto grande de autos, un mensaje y el boton para ir a verlos. */
@Composable
private fun CarritoVacio(onVerAutos: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(28.dp))) {
            Image(
                painter = painterResource(R.drawable.carrito_autos),
                contentDescription = "Autos en pista",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(0.35f to Color.Transparent, 1f to SmokyBlack.copy(alpha = 0.88f))
                )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall, color = FloralWhite)
                Text(
                    "Elige un auto, un color y compra o agenda una cita.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FloralWhite.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Button(onClick = onVerAutos, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("Explorar autos")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Beneficio(Icons.Filled.Palette, "Elige tu color", Modifier.weight(1f))
            Beneficio(Icons.Filled.CalendarMonth, "Agenda tu cita", Modifier.weight(1f))
            Beneficio(Icons.Filled.Verified, "Compra segura", Modifier.weight(1f))
        }
    }
}

@Composable
private fun Beneficio(icono: ImageVector, texto: String, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(texto, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp), maxLines = 2)
        }
    }
}

@Composable
private fun AutoEnCarrito(auto: Auto, onClick: () -> Unit, onQuitar: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FotoAuto(auto.imagenUrl, Modifier.size(width = 118.dp, height = 92.dp).clip(RoundedCornerShape(16.dp)))
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(
                    auto.marca.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(auto.modelo.ifBlank { auto.marca }, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                if (auto.estado == EstadoAuto.DISPONIBLE) {
                    Text(
                        precioEnSoles(auto.precio),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text("Ya no está disponible", color = MaterialTheme.colorScheme.error)
                }
            }
            IconButton(onClick = onQuitar) {
                Icon(Icons.Filled.Close, contentDescription = "Quitar del carrito")
            }
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.titleMedium)
    }
}
