package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.Unidad
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.EstadoPedidoChip
import com.flores.lavacampus.ui.components.IconoServicio
import com.flores.lavacampus.ui.components.StepperEstadoVertical
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.ui.components.fechaHora
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.DetallePedidoViewModel
import java.util.Locale

// La View solo observa el pedido y avisa la cancelacion; la regla esta en el ViewModel
@Composable
fun DetallePedidoScreen(vm: DetallePedidoViewModel, onVolver: () -> Unit) {
    val detalle by vm.pedido.collectAsState()
    val puedeCancelar by vm.puedeCancelar.collectAsState()
    var confirmando by remember { mutableStateOf(false) }

    val d = detalle
    Column(Modifier.fillMaxSize()) {
        AppTopBar(if (d == null) "Pedido" else "Pedido ${d.pedido.id.codigoPedido()}", onVolver)
        if (d == null) return@Column

        val p = d.pedido
        val estado = EstadoPedido.valueOf(p.estado)
        val primario = MaterialTheme.colorScheme.primary
        val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabecera: fecha de registro, entrega estimada y estado
            Tarjeta {
                Row(
                    Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(estado.color.copy(alpha = 0.15f))
                    ) { IconoServicio("wash", estado.color, 32.dp) }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            "Registrado el ${p.fechaRegistro.fechaCorta()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text("Entrega estimada: ${p.fechaEntrega.fechaCorta()}", fontSize = 12.sp, color = gris)
                        EstadoPedidoChip(estado)
                    }
                }
            }

            Text("Seguimiento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Tarjeta {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    StepperEstadoVertical(
                        estado = estado,
                        colorActivo = primario,
                        colorInactivo = Color(0xFFCBD5E1),
                        colorTexto = Color(0xFF1E293B),
                        // Solo se guarda la hora de registro; de las demas etapas no hay marca de tiempo
                        textoDerecha = { etapa ->
                            val alcanzada = estado != EstadoPedido.CANCELADO && etapa.ordinal <= estado.ordinal
                            when {
                                etapa == EstadoPedido.RECIBIDO && alcanzada -> p.fechaCreacion.fechaHora()
                                alcanzada -> "Completado"
                                else -> "Pendiente"
                            }
                        }
                    )
                }
            }

            Text("Detalle", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Tarjeta {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    d.detalles.forEach { linea ->
                        val u = Unidad.de(linea.servicio.unidad)
                        val c = linea.detalle.cantidad
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${linea.servicio.nombre} · ${texto(c)} ${if (c == 1.0) u.texto else u.plural} × ${linea.detalle.precioUnitario.soles()}",
                                fontSize = 12.sp,
                                color = gris,
                                modifier = Modifier.weight(1f)
                            )
                            Text(linea.detalle.subtotal.soles(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(p.total.soles(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = primario)
                    }
                }
            }

            RecuadroPago(d)

            OutlinedButton(
                onClick = { confirmando = true },
                enabled = puedeCancelar,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancelar pedido", fontWeight = FontWeight.SemiBold) }

            // Aviso cuando la regla no permite cancelar
            if (!puedeCancelar && estado != EstadoPedido.CANCELADO) {
                Text(
                    "Solo se cancela antes de empezar el lavado",
                    fontSize = 11.sp,
                    color = gris,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("¿Cancelar este pedido?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { vm.cancelar(); confirmando = false }) { Text("Sí, cancelar") }
            },
            dismissButton = { TextButton(onClick = { confirmando = false }) { Text("No") } }
        )
    }
}

@Composable
private fun Tarjeta(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) { contenido() }
}

// Recuadro de pago y casillero
@Composable
private fun RecuadroPago(d: PedidoConDetalle) {
    val p = d.pedido
    val estado = EstadoPedido.valueOf(p.estado)
    val pagado = p.pagado
    val cancelado = estado == EstadoPedido.CANCELADO
    val fondo = when {
        cancelado -> Color(0xFFFEE2E2)
        pagado -> Color(0xFFDCFCE7)
        else -> Color(0xFFFEF3C7)
    }
    val titulo = when {
        cancelado -> Color(0xFF991B1B)
        pagado -> Color(0xFF166534)
        else -> Color(0xFF92400E)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            when {
                cancelado -> "Pedido cancelado"
                pagado -> "Pagado"
                else -> "Pago pendiente · se paga al recoger"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = titulo
        )
        // Casillero: asignado si esta LISTO; si no, se avisa cuando se asignara
        when {
            p.casillero != null -> Text("Casillero: ${p.casillero}", fontSize = 12.sp, color = titulo)
            estado != EstadoPedido.CANCELADO ->
                Text("Casillero: se asignará al estar listo", fontSize = 12.sp, color = titulo)
        }
    }
}

// 3.5 -> "3.5", 5.0 -> "5"
private fun texto(c: Double): String =
    if (c % 1.0 == 0.0) c.toInt().toString() else String.format(Locale.US, "%.1f", c)
