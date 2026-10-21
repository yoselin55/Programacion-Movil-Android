package com.flores.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.EstadoPedidoChip
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.ui.components.nombreCorto
import com.flores.lavacampus.ui.components.resumenCantidad
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.BandejaViewModel

// La View solo muestra la lista filtrada y avisa los clics
@Composable
fun BandejaScreen(vm: BandejaViewModel, onVolver: () -> Unit, onAbrir: (Int) -> Unit) {
    val pedidos by vm.pedidos.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val atrasados by vm.atrasados.collectAsState()
    val primario = MaterialTheme.colorScheme.primary

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Pedidos", onVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Buscador por nombre del estudiante o numero de pedido
            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar por estudiante o número", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = primario) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primario,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Chips de filtro
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Filtro("Todos", filtro == null) { vm.elegirFiltro(null) }
                Filtro("Recibido", filtro == EstadoPedido.RECIBIDO) { vm.elegirFiltro(EstadoPedido.RECIBIDO) }
                Filtro("En proceso", filtro == EstadoPedido.EN_PROCESO) { vm.elegirFiltro(EstadoPedido.EN_PROCESO) }
                Filtro("Listo", filtro == EstadoPedido.LISTO) { vm.elegirFiltro(EstadoPedido.LISTO) }
            }

            if (pedidos.isEmpty()) {
                Text(
                    "No hay pedidos para mostrar",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
            pedidos.forEach { p ->
                TarjetaBandeja(
                    p,
                    atrasado = p.pedido.id in atrasados,
                    onClick = { onAbrir(p.pedido.id) },
                    onIniciar = { vm.iniciar(p) }
                )
            }
        }
    }
}

@Composable
private fun Filtro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val primario = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) primario else Color.White)
            .border(1.dp, if (seleccionado) primario else Color(0xFFE2E8F0), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else Color(0xFF475569)
        )
    }
}

// Tarjeta: estudiante, detalle, total, estado y un boton de accion segun el estado
@Composable
private fun TarjetaBandeja(
    p: PedidoConDetalle,
    atrasado: Boolean,
    onClick: () -> Unit,
    onIniciar: () -> Unit
) {
    val estado = EstadoPedido.valueOf(p.pedido.estado)
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val primario = MaterialTheme.colorScheme.primary

    // Boton de accion segun el estado
    val accion = when (estado) {
        EstadoPedido.RECIBIDO -> "Iniciar"
        EstadoPedido.EN_PROCESO -> "Marcar listo"
        EstadoPedido.LISTO -> "Entregar"
        else -> null
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Atrasado: borde y fondo rojos
        border = BorderStroke(if (atrasado) 1.5.dp else 1.dp, if (atrasado) EstadoPedido.CANCELADO.color else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(estado.color.copy(alpha = 0.12f))
            ) { Icon(Icons.Outlined.LocalLaundryService, null, tint = estado.color) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Pedido ${p.pedido.id.codigoPedido()}", fontWeight = FontWeight.Bold)
                Text(
                    "${p.usuario.nombre.nombreCorto()} · ${p.resumenCantidad()}",
                    fontSize = 12.sp,
                    color = gris
                )
                EstadoPedidoChip(estado)
                if (atrasado) {
                    Text(
                        "Atrasado · era para el ${p.pedido.fechaEntrega.fechaCorta()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EstadoPedido.CANCELADO.color
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(p.pedido.total.soles(), fontWeight = FontWeight.Bold, color = primario)
                if (accion != null) {
                    Button(
                        // Iniciar cambia el estado aqui; las otras dos acciones abren la gestion
                        // porque exigen casillero y cobro
                        onClick = if (estado == EstadoPedido.RECIBIDO) onIniciar else onClick,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) { Text(accion, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}
