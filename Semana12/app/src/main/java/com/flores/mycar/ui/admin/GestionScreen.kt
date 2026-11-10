package com.flores.mycar.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.EstadoCita
import com.flores.mycar.domain.model.fechaPasada
import com.flores.mycar.ui.components.EtiquetaEstado
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.IconoAtras
import com.flores.mycar.ui.components.precioEnSoles

/**
 * Gestion del admin: cambia el estado de los pedidos (pago, entrega, si la persona llego a recoger su auto)
 * y confirma las citas. Ve quien compro, quien agendo y para que fecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionScreen(
    viewModel: GestionViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Pedidos y citas") },
                navigationIcon = { IconoAtras(onVolver) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                com.flores.mycar.ui.components.CarruselFotos(
                    fotos = listOf(com.flores.mycar.R.drawable.admin_cullinan, com.flores.mycar.R.drawable.admin_defender),
                    titulo = "Pedidos y citas",
                    subtitulo = "Quién compró, qué auto y para cuándo.",
                    alto = 150
                )
            }
            estado.mensaje?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }

            item { Text("Pedidos (${estado.autos.size})", style = MaterialTheme.typography.titleLarge) }
            if (estado.autos.isEmpty()) {
                item { Text("Todavía no hay pedidos.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(estado.autos, key = { "auto_" + it.id }) { auto ->
                PedidoAdmin(auto) { nuevo -> viewModel.cambiarEstado(auto, nuevo) }
            }

            item { Text("Citas (${estado.citas.size})", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp)) }
            if (estado.citas.isEmpty()) {
                item { Text("No hay citas agendadas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(estado.citas, key = { "cita_" + it.id }) { cita ->
                CitaAdmin(cita) { viewModel.confirmarCita(cita) }
            }
        }
    }
}

@Composable
private fun PedidoAdmin(auto: Auto, onCambiar: (EstadoAuto) -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FotoAuto(auto.imagenUrl, Modifier.size(width = 96.dp, height = 72.dp).clip(RoundedCornerShape(14.dp)))
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(auto.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                    Text(
                        if (auto.color.isBlank()) precioEnSoles(auto.precio) else "${precioEnSoles(auto.precio)} · ${auto.color}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                EtiquetaEstado(auto.estado)
            }

            Fila(Icons.Filled.Person, auto.compradorCorreo ?: "Cliente")
            if (auto.direccion.isNotBlank()) {
                Fila(Icons.Filled.LocalShipping, "Envío a: ${auto.direccion}" + if (auto.recojo.isNotBlank()) " el ${auto.recojo}" else "")
            } else if (auto.recojo.isNotBlank()) {
                Fila(
                    Icons.Filled.Storefront,
                    "Va a recoger su auto el ${auto.recojo}" + if (fechaPasada(auto.recojo)) "  (ya pasó)" else ""
                )
            }

            when (auto.estado) {
                EstadoAuto.PAGO_REALIZADO -> Button(
                    onClick = { onCambiar(EstadoAuto.PAGADO) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(50.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Confirmar pago", modifier = Modifier.padding(start = 8.dp))
                }

                EstadoAuto.PAGADO -> if (auto.recojo.isNotBlank()) {
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onCambiar(EstadoAuto.ENTREGADO) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Filled.LocalShipping, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Llegó", modifier = Modifier.padding(start = 6.dp))
                        }
                        OutlinedButton(
                            onClick = { onCambiar(EstadoAuto.NO_RECOGIDO) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("No llegó", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                } else {
                    Button(
                        onClick = { onCambiar(EstadoAuto.ENTREGADO) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(50.dp)
                    ) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Marcar como entregado", modifier = Modifier.padding(start = 8.dp))
                    }
                }

                EstadoAuto.NO_RECOGIDO -> OutlinedButton(
                    onClick = { onCambiar(EstadoAuto.PAGADO) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(50.dp)
                ) { Text("Volver a pagado") }

                else -> Unit
            }
        }
    }
}

@Composable
private fun CitaAdmin(cita: Cita, onConfirmar: () -> Unit) {
    val paso = fechaPasada(cita.fecha)
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (cita.estado == EstadoCita.CONFIRMADA) Icons.Filled.EventAvailable else Icons.Filled.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(cita.autoNombre, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                    Text("${cita.fecha} a las ${cita.hora}", style = MaterialTheme.typography.bodyLarge)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        cita.estado == EstadoCita.CONFIRMADA && paso -> MaterialTheme.colorScheme.tertiaryContainer
                        cita.estado == EstadoCita.CONFIRMADA -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        if (cita.estado == EstadoCita.CONFIRMADA && paso) "Cumplida" else cita.estado.etiqueta,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
            Fila(Icons.Filled.Person, listOf(cita.nombre, cita.correo).filter { it.isNotBlank() }.joinToString(" · "))
            if (cita.nota.isNotBlank()) {
                Text("“${cita.nota}”", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            }
            if (cita.estado == EstadoCita.PENDIENTE) {
                Button(
                    onClick = onConfirmar,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(50.dp)
                ) {
                    Icon(Icons.Filled.EventAvailable, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Confirmar cita", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun Fila(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 10.dp))
    }
}
