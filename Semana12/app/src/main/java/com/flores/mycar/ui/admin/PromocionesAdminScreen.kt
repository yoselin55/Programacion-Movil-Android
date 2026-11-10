package com.flores.mycar.ui.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.data.autos.MarcasEnVenta
import com.flores.mycar.domain.model.Promocion
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.IconoAtras

private val MESES = listOf("Noviembre", "Diciembre", "Enero", "Febrero", "Marzo")

/** Promociones: descuentos por mes en todas las marcas o en una. Se pueden activar y desactivar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromocionesAdminScreen(
    viewModel: PromocionesAdminViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val promociones by viewModel.promociones.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var creando by rememberSaveable { mutableStateOf(false) }

    val avisos = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            viewModel.errorMostrado()
            avisos.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Promociones") }, navigationIcon = { IconoAtras(onVolver) }) },
        snackbarHost = { SnackbarHost(avisos) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { creando = true }, icon = { Icon(Icons.Filled.Add, null) }, text = { Text("Nueva promoción") })
        }
    ) { padding ->
        val lista = promociones
        if (lista == null) {
            Box(Modifier.padding(padding).fillMaxSize()) { Cargando() }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    com.flores.mycar.ui.components.CarruselFotos(
                        fotos = listOf(com.flores.mycar.R.drawable.promo_bmw, com.flores.mycar.R.drawable.promo_evoque),
                        titulo = "Promociones",
                        subtitulo = "Se ven en el inicio y bajan el precio de los autos.",
                        alto = 170
                    )
                }
                if (lista.isEmpty()) {
                    item {
                        Button(onClick = viewModel::cargarEjemplos, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(54.dp)) {
                            Icon(Icons.Filled.LocalOffer, contentDescription = null)
                            Text("Cargar 3 promociones de ejemplo", modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                }
                items(lista, key = { it.id }) { promo ->
                    FilaPromocion(promo, { viewModel.alternar(promo, it) }, { viewModel.eliminar(promo) })
                }
            }
        }
    }

    if (creando) {
        DialogoPromocion(
            onGuardar = { viewModel.crear(it); creando = false },
            onCancelar = { creando = false }
        )
    }
}

@Composable
private fun FilaPromocion(promo: Promocion, onActiva: (Boolean) -> Unit, onEliminar: () -> Unit) {
    val fondo by animateColorAsState(
        if (promo.activa) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "fondo"
    )
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("-${promo.descuento}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(promo.titulo, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(
                    "${promo.mes} · ${promo.marca.ifBlank { "Todas las marcas" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(if (promo.activa) "Activa" else "Desactivada", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(checked = promo.activa, onCheckedChange = onActiva)
                IconButton(onClick = onEliminar) { Icon(Icons.Filled.Delete, contentDescription = "Borrar promoción") }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DialogoPromocion(onGuardar: (Promocion) -> Unit, onCancelar: () -> Unit) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descuento by rememberSaveable { mutableStateOf("10") }
    var mes by rememberSaveable { mutableStateOf(MESES.first()) }
    var marca by rememberSaveable { mutableStateOf("") }
    val porcentaje = descuento.toIntOrNull()
    val valido = titulo.isNotBlank() && porcentaje != null && porcentaje in 1..90

    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Filled.LocalOffer, contentDescription = null) },
        title = { Text("Nueva promoción") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(
                    value = descuento,
                    onValueChange = { descuento = it.filter(Char::isDigit).take(2) },
                    label = { Text("Descuento (%)") },
                    singleLine = true
                )
                Text("Mes", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MESES.forEach { FilterChip(selected = mes == it, onClick = { mes = it }, label = { Text(it) }) }
                }
                Text("Marca", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = marca.isEmpty(), onClick = { marca = "" }, label = { Text("Todas") })
                    MarcasEnVenta.principales.forEach { FilterChip(selected = marca == it, onClick = { marca = it }, label = { Text(it) }) }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    onGuardar(
                        Promocion(
                            titulo = titulo.trim(),
                            descripcion = if (marca.isBlank()) "En todos los autos" else "En autos $marca",
                            descuento = porcentaje ?: 0,
                            mes = mes,
                            marca = marca
                        )
                    )
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
