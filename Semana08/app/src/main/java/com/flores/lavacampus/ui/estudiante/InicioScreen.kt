package com.flores.lavacampus.ui.estudiante

import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.EstadoVacio
import com.flores.lavacampus.ui.components.AvatarUsuario
import com.flores.lavacampus.data.prefs.FotoPerfil
import com.flores.lavacampus.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.ui.components.LavaButton
import com.flores.lavacampus.ui.components.PedidoCard
import com.flores.lavacampus.ui.components.StepperEstado
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.ui.components.fechaCorta
import com.flores.lavacampus.viewmodel.InicioViewModel

// La View solo observa el ViewModel y avisa los clics
@Composable
fun InicioScreen(
    vm: InicioViewModel,
    onNuevoPedido: () -> Unit,
    onVerPedido: (Int) -> Unit,
    onMisPedidos: () -> Unit,
    onTarifas: () -> Unit,
    foto: FotoPerfil? = null
) {
    // null = todavia cargando: no se muestran pantallas vacias hasta saber si hay pedidos
    val ui by vm.ui.collectAsState()
    val cargado = ui != null
    val activo = ui?.activo
    val recientes = ui?.recientes ?: emptyList()
    val usuario = vm.usuario
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner con la foto de la lavanderia, el saludo y la foto de perfil
        FotoConDegradado(
            R.drawable.foto_lavanderia,
            Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(22.dp))
        ) {
            Row(Modifier.fillMaxSize().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                AvatarUsuario(usuario.nombre, foto, 68.dp)
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(
                        "Hola, ${usuario.nombre.substringBefore(" ")}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("${usuario.codigo} · Tecsup", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                }
                Icon(Icons.Outlined.Notifications, "Avisos", tint = Color.White)
            }
        }

        // Hasta que Room entrega los pedidos solo se ve el banner (no depende de ellos).
        // Asi nunca aparece una pantalla intermedia: el resto entra completo de una vez
        if (!cargado) return@Column

        // Tarjeta turquesa del pedido activo
        Card(
            onClick = { activo?.let { onVerPedido(it.pedido.id) } },
            enabled = activo != null,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val p = activo
                val claro = MaterialTheme.colorScheme.onPrimary
                if (p == null) {
                    Text("Tu pedido activo", color = claro.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(
                        "No tienes pedidos activos",
                        color = claro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    val estado = EstadoPedido.valueOf(p.pedido.estado)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tu pedido activo", color = claro.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(
                            p.pedido.id.codigoPedido(),
                            color = claro,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(estado.texto, color = claro, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Entrega estimada: ${p.pedido.fechaEntrega.fechaCorta()}",
                        color = claro.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                    // Seguimiento dentro de un recuadro blanco
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                    ) {
                        StepperEstado(
                            estado = estado,
                            colorActivo = MaterialTheme.colorScheme.primary,
                            colorInactivo = Color(0xFFCBD5E1),
                            colorTexto = Color(0xFF1E293B)
                        )
                    }
                    // Pedido LISTO: indica donde recogerlo
                    if (estado == EstadoPedido.LISTO && p.pedido.casillero != null) {
                        Text(
                            "Recógelo en el casillero ${p.pedido.casillero}",
                            color = claro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        LavaButton("Nuevo pedido", onNuevoPedido, Modifier.fillMaxWidth())

        // Accesos rapidos
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Acceso("Mis pedidos", "Ver estado", Icons.Outlined.Menu, onMisPedidos, Modifier.weight(1f))
            Acceso("Tarifas", "Precios por kg", Icons.Outlined.Sell, onTarifas, Modifier.weight(1f))
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Últimos pedidos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Ver todos",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onMisPedidos() }
            )
        }
        if (cargado && recientes.isEmpty()) EstadoVacio("Aún no tienes pedidos", "Cuando hagas uno, aparecerá aquí")
        recientes.forEach { PedidoCard(it, onClick = { onVerPedido(it.pedido.id) }) }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Acceso(
    titulo: String,
    detalle: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(icono, null, tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    detalle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
