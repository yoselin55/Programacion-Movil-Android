package com.flores.lavacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.model.EstadoPedido

// Tarjeta resumen de un pedido (se reutiliza en Inicio, Mis pedidos y Bandeja)
// modoLista: estilo de "Mis pedidos" (borde y pastilla verde en LISTO, entrega en EN_PROCESO)
// onCancelar: si no es nulo y el pedido esta RECIBIDO, muestra el enlace "Cancelar"
@Composable
fun PedidoCard(
    pedido: PedidoConDetalle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    modoLista: Boolean = false,
    onCancelar: (() -> Unit)? = null
) {
    val p = pedido.pedido
    val estado = EstadoPedido.valueOf(p.estado)
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val resaltar = modoLista && estado == EstadoPedido.LISTO

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            if (resaltar) 1.5.dp else 1.dp,
            if (resaltar) EstadoPedido.LISTO.color else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono con el color del estado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(estado.color.copy(alpha = 0.12f))
            ) {
                Icon(Icons.Outlined.LocalLaundryService, null, tint = estado.color)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Pedido ${p.id.codigoPedido()}", fontWeight = FontWeight.Bold)
                Text(
                    "${pedido.resumenCantidad()} · ${p.fechaRegistro.fechaCorta().replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = gris
                )
                EstadoPedidoChip(estado)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(p.total.soles(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                when {
                    // Pedido LISTO: se destaca el casillero
                    estado == EstadoPedido.LISTO && p.casillero != null && modoLista ->
                        Surface(shape = RoundedCornerShape(50), color = EstadoPedido.LISTO.color) {
                            Text(
                                "Casillero ${p.casillero}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    estado == EstadoPedido.LISTO && p.casillero != null ->
                        Text(
                            "Casillero ${p.casillero}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    modoLista && estado == EstadoPedido.EN_PROCESO ->
                        Text(
                            "Entrega ${p.fechaEntrega.fechaCorta()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    estado == EstadoPedido.RECIBIDO && onCancelar != null ->
                        Text(
                            "Cancelar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EstadoPedido.CANCELADO.color,
                            modifier = Modifier.clickable { onCancelar() }
                        )
                    else -> Box {}
                }
            }
        }
    }
}
