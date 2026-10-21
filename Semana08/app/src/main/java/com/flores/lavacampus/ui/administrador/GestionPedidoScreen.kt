package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.Casilleros
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.EstadoPedidoChip
import com.flores.lavacampus.ui.components.StepperEstadoVertical
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.ui.components.fechaHora
import com.flores.lavacampus.ui.components.iniciales
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.GestionPedidoViewModel

// La View solo muestra el estado; las reglas de casillero y cobro estan en el ViewModel
@Composable
fun GestionPedidoScreen(vm: GestionPedidoViewModel, onVolver: () -> Unit) {
    val detalle by vm.pedido.collectAsState()
    val ocupados by vm.casillerosOcupados.collectAsState()
    val elegido by vm.casillero.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val primario = MaterialTheme.colorScheme.primary
    val rojo = EstadoPedido.CANCELADO.color

    val d = detalle
    Column(Modifier.fillMaxSize()) {
        AppTopBar(if (d == null) "Gestionar pedido" else "Gestionar pedido ${d.pedido.id.codigoPedido()}", onVolver)
        if (d == null) return@Column

        val p = d.pedido
        val estado = EstadoPedido.valueOf(p.estado)
        val siguiente = estado.siguiente()
        val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Datos del estudiante
            Tarjeta {
                Row(
                    Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(44.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) { Text(d.usuario.nombre.iniciales(), color = primario, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(d.usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(d.usuario.codigo, fontSize = 11.sp, color = gris)
                    }
                    EstadoPedidoChip(estado)
                }
            }

            // Seguimiento vertical
            Tarjeta {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    StepperEstadoVertical(
                        estado = estado,
                        colorActivo = primario,
                        colorInactivo = Color(0xFFCBD5E1),
                        colorTexto = Color(0xFF1E293B),
                        textoDerecha = { etapa ->
                            val alcanzada = estado != EstadoPedido.CANCELADO && etapa.ordinal <= estado.ordinal
                            when {
                                etapa == EstadoPedido.RECIBIDO && alcanzada -> p.fechaCreacion.fechaHora()
                                alcanzada -> "Completado"
                                else -> "—"
                            }
                        }
                    )
                }
            }

            // Selector de casillero (cuando el pedido esta por pasar a LISTO o ya esta LISTO)
            if (estado == EstadoPedido.EN_PROCESO || estado == EstadoPedido.LISTO) {
                Text("Casillero para el recojo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                // Una sola fila con desplazamiento horizontal (C-01 a C-30)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    Casilleros.todos.forEach { c ->
                        val ocupado = c in ocupados
                        val activo = c == elegido
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(72.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    when {
                                        activo -> primario
                                        ocupado -> Color(0xFFF1F5F9)
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (activo) primario else Color(0xFFE2E8F0),
                                    RoundedCornerShape(50)
                                )
                                // Los ocupados no responden al toque
                                .then(if (ocupado) Modifier else Modifier.clickable { vm.elegirCasillero(c) })
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                c,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    activo -> MaterialTheme.colorScheme.onPrimary
                                    ocupado -> Color(0xFF94A3B8)
                                    else -> Color(0xFF1E293B)
                                }
                            )
                        }
                    }
                }
                Text("Los casilleros en gris ya están ocupados", fontSize = 11.sp, color = gris)
            }

            // Total y cobro
            Tarjeta {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Total ${p.total.soles()}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            if (p.descuento > 0) "Incluye descuento de ${p.descuento.soles()} · se cobra al entregar"
                            else "Se cobra al entregar",
                            fontSize = 11.sp,
                            color = gris
                        )
                    }
                    Text("Cobrado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(Modifier.padding(start = 8.dp)) {
                        Switch(
                            checked = p.pagado,
                            onCheckedChange = vm::marcarPagado,
                            enabled = estado != EstadoPedido.CANCELADO
                        )
                    }
                }
            }

            // Recordatorio de la regla
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                    .padding(12.dp)
            ) {
                Text("Regla: no se entrega un pedido sin cobrar,", fontSize = 11.sp, color = gris)
                Text(
                    "ni se pasa a Listo sin casillero.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = primario
                )
            }

            // Motivo si una regla rechaza el cambio
            mensaje?.let {
                Text(it, color = rojo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }

            if (siguiente != null) {
                Button(
                    onClick = vm::avanzar,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) { Text("Marcar como ${siguiente.texto}", fontWeight = FontWeight.SemiBold) }
            }
            if (estado == EstadoPedido.RECIBIDO || estado == EstadoPedido.EN_PROCESO) {
                OutlinedButton(
                    onClick = vm::cancelar,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, rojo),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = rojo),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cancelar pedido", fontWeight = FontWeight.SemiBold) }
            }
        }
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
