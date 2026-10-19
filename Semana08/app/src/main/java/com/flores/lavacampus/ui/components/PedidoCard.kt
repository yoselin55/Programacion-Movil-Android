package com.flores.lavacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
@Composable
fun PedidoCard(
    pedido: PedidoConDetalle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val p = pedido.pedido
    val estado = EstadoPedido.valueOf(p.estado)
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(p.total.soles(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                // Pedido LISTO: se destaca el casillero
                if (estado == EstadoPedido.LISTO && p.casillero != null) {
                    Text(
                        "Casillero ${p.casillero}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
