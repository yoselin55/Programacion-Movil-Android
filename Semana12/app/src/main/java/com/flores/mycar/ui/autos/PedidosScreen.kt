package com.flores.mycar.ui.autos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.EstadoCita
import com.flores.mycar.domain.model.fechaPasada
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.IconoAtras
import com.flores.mycar.ui.components.PuntoColor
import com.flores.mycar.ui.components.precioEnSoles
import kotlinx.coroutines.delay

/** Mis pedidos: el seguimiento de cada compra (en proceso, pagado, entregado) y las citas. Todo se actualiza en tiempo real. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    viewModel: PedidosViewModel,
    uid: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(uid) { viewModel.observar(uid) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos") },
                navigationIcon = { IconoAtras(onVolver) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            estado.error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }

            item { Resumen(estado.compras) }

            item { Titulo("Mis compras", estado.compras.size) }
            if (estado.compras.isEmpty()) {
                item { Vacio(Icons.Filled.DirectionsCar, "Todavía no has hecho ningún pedido.") }
            }
            items(estado.compras, key = { "auto_" + it.id }) { auto ->
                val indice = estado.compras.indexOf(auto)
                Aparecer(indice, Modifier.animateItem()) { PedidoCard(auto) }
            }

            item { Titulo("Mis citas", estado.citas.size) }
            if (estado.citas.isEmpty()) {
                item { Vacio(Icons.Filled.Event, "Todavía no has agendado ninguna cita.") }
            }
            items(estado.citas, key = { "cita_" + it.id }) { cita ->
                val indice = estado.citas.indexOf(cita)
                Aparecer(indice, Modifier.animateItem()) { CitaCard(cita) }
            }
        }
    }
}

/** Hace que el contenido aparezca suavemente (desvanece y sube un poco), uno tras otro. */
@Composable
private fun Aparecer(indice: Int, modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(indice.coerceAtMost(8) * 80L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 5 }
    ) { contenido() }
}

@Composable
private fun Titulo(texto: String, cantidad: Int) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(texto, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
            Text(
                cantidad.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun Vacio(icono: ImageVector, texto: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 14.dp))
        }
    }
}

/** Contadores de arriba: cuantos pedidos hay en cada etapa. */
@Composable
private fun Resumen(compras: List<Auto>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Contador("En proceso", compras.count { it.estado == EstadoAuto.PAGO_REALIZADO }, Icons.Filled.HourglassTop, Modifier.weight(1f))
        Contador("Pagados", compras.count { it.estado == EstadoAuto.PAGADO }, Icons.Filled.Payments, Modifier.weight(1f))
        Contador("Entregados", compras.count { it.estado == EstadoAuto.ENTREGADO }, Icons.Filled.LocalShipping, Modifier.weight(1f))
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, icono: ImageVector, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            // El numero cambia con una transicion cuando el estado de un pedido se actualiza.
            AnimatedContent(
                targetState = valor,
                transitionSpec = { slideInVertically(tween(300)) { it } + fadeIn(tween(300)) togetherWith fadeOut(tween(150)) },
                label = "contador"
            ) { v -> Text(v.toString(), style = MaterialTheme.typography.headlineMedium) }
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@Composable
private fun PedidoCard(auto: Auto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FotoAuto(auto.imagenUrl, Modifier.size(width = 112.dp, height = 84.dp).clip(RoundedCornerShape(16.dp)))
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(auto.marca.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(auto.modelo.ifBlank { auto.marca }, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Text(precioEnSoles(auto.precio), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        if (auto.color.isNotBlank()) {
                            PuntoColor(auto.color, tamano = 12.dp, modifier = Modifier.padding(start = 10.dp, end = 4.dp))
                            Text(auto.color, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Seguimiento(auto.estado, Modifier.padding(top = 16.dp))
            if (auto.direccion.isNotBlank() || auto.recojo.isNotBlank()) {
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (auto.direccion.isNotBlank()) Icons.Filled.LocalShipping else Icons.Filled.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        if (auto.direccion.isNotBlank()) "Te lo llevamos a: ${auto.direccion}" + if (auto.recojo.isNotBlank()) " el ${auto.recojo}" else "" else "Recoges tu auto el ${auto.recojo}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
            when (auto.estado) {
                EstadoAuto.ENTREGADO -> Aviso("¡Auto comprado! Ya es tuyo.", Icons.Filled.CheckCircle, false)
                EstadoAuto.NO_RECOGIDO -> Aviso("No recogiste tu auto en la fecha indicada.", Icons.Filled.Close, true)
                else -> Unit
            }
        }
    }
}

/** Barra de progreso con tres etapas. La barra y los colores se animan cuando cambia el estado. */
@Composable
private fun Seguimiento(estado: EstadoAuto, modifier: Modifier = Modifier) {
    val paso = when (estado) {
        EstadoAuto.PAGADO -> 1
        EstadoAuto.ENTREGADO, EstadoAuto.NO_RECOGIDO -> 2
        else -> 0
    }
    var iniciado by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { iniciado = true }
    val progreso by animateFloatAsState(
        targetValue = if (iniciado) (paso + 1) / 3f else 0f,
        animationSpec = tween(900),
        label = "progreso"
    )
    val etapas = listOf(
        "En proceso" to Icons.Filled.HourglassTop,
        "Pagado" to Icons.Filled.Payments,
        "Comprado" to Icons.Filled.LocalShipping
    )
    Column(modifier) {
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            etapas.forEachIndexed { i, (titulo, icono) ->
                val falla = estado == EstadoAuto.NO_RECOGIDO && i == 2
                val color by animateColorAsState(
                    if (falla) MaterialTheme.colorScheme.error else if (i <= paso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    tween(500),
                    label = "etapa"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(if (falla) Icons.Filled.Close else if (i < paso) Icons.Filled.CheckCircle else icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                    Text(titulo, style = MaterialTheme.typography.labelMedium, color = color, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun Aviso(texto: String, icono: ImageVector, problema: Boolean) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (problema) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null)
            Text(texto, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(start = 10.dp))
        }
    }
}

@Composable
private fun CitaCard(cita: Cita) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(52.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (cita.estado == EstadoCita.CONFIRMADA) Icons.Filled.EventAvailable else Icons.Filled.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(cita.autoNombre, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(
                    "${cita.fecha} a las ${cita.hora}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // El estado cambia con una transicion cuando el administrador confirma la cita.
            val etiqueta = if (cita.estado == EstadoCita.CONFIRMADA && fechaPasada(cita.fecha)) "Cumplida" else cita.estado.etiqueta
            AnimatedContent(
                targetState = etiqueta,
                transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(200)) },
                label = "estadoCita"
            ) { e ->
                val confirmada = e != EstadoCita.PENDIENTE.etiqueta
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (confirmada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        e,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (confirmada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
