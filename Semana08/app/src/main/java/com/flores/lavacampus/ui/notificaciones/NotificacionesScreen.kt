package com.flores.lavacampus.ui.notificaciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.delay
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.flores.lavacampus.data.local.entity.Notificacion
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.TipoNotificacion
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.EstadoVacio
import com.flores.lavacampus.ui.components.momento
import com.flores.lavacampus.viewmodel.NotificacionesViewModel

// Lista de avisos del usuario: las nuevas arriba y debajo las ya leidas, cada una con su hora.
// Al tocar una se marca como leida y se abre su pedido
@Composable
fun NotificacionesScreen(vm: NotificacionesViewModel, onVolver: () -> Unit, onAbrirPedido: (Int) -> Unit) {
    val lista by vm.notificaciones.collectAsState()
    val sinLeer by vm.noLeidas.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Notificaciones", onVolver)
        // Hasta que Room responde solo se ve la barra: nunca "sin avisos" por error
        val avisos = lista ?: return@Column

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (sinLeer == 0) "Todo al día" else if (sinLeer == 1) "1 sin leer" else "$sinLeer sin leer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        "${avisos.size} en total",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
                if (sinLeer > 0) {
                    TextButton(onClick = vm::marcarTodasLeidas) {
                        Text("Marcar todas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            if (avisos.isEmpty()) {
                EstadoVacio("Aún no tienes notificaciones", "Aquí verás los cambios de tus pedidos al instante")
            }

            val nuevas = avisos.filter { !it.leida }
            val anteriores = avisos.filter { it.leida }
            if (nuevas.isNotEmpty()) {
                Seccion("Nuevas")
                nuevas.forEach { n -> Tarjeta(n) { vm.marcarLeida(n.id); n.pedidoId?.let(onAbrirPedido) } }
            }
            if (anteriores.isNotEmpty()) {
                Seccion("Anteriores")
                anteriores.forEach { n -> Tarjeta(n) { n.pedidoId?.let(onAbrirPedido) } }
            }
            if (avisos.isNotEmpty()) {
                TextButton(onClick = vm::borrarTodas, modifier = Modifier.fillMaxWidth()) {
                    Text("Borrar todas", color = EstadoPedido.CANCELADO.color, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(
        titulo,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        modifier = Modifier.padding(top = 4.dp)
    )
}

// Icono y color de cada tipo de aviso
@Composable
private fun estiloDe(tipo: TipoNotificacion): Pair<ImageVector, Color> = when (tipo) {
    TipoNotificacion.NUEVO -> Icons.Outlined.Inbox to MaterialTheme.colorScheme.primary
    TipoNotificacion.RECIBIDO -> Icons.Outlined.Inbox to EstadoPedido.RECIBIDO.color
    TipoNotificacion.EN_PROCESO -> Icons.Outlined.Autorenew to EstadoPedido.EN_PROCESO.color
    TipoNotificacion.LISTO -> Icons.Outlined.CheckCircle to EstadoPedido.LISTO.color
    TipoNotificacion.ENTREGADO -> Icons.Outlined.DoneAll to EstadoPedido.LISTO.color
    TipoNotificacion.CANCELADO -> Icons.Outlined.Cancel to EstadoPedido.CANCELADO.color
    TipoNotificacion.PAGO -> Icons.Outlined.Payments to EstadoPedido.LISTO.color
    TipoNotificacion.ALERTA -> Icons.Outlined.Warning to EstadoPedido.CANCELADO.color
}

@Composable
private fun Tarjeta(n: Notificacion, onClick: () -> Unit) {
    val (icono, color) = estiloDe(TipoNotificacion.de(n.tipo))
    val gris = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (n.leida) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, if (n.leida) MaterialTheme.colorScheme.outlineVariant else color.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                Modifier.size(46.dp).clip(CircleShape).background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) { Icon(icono, null, tint = color, modifier = Modifier.size(24.dp)) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(n.titulo, fontWeight = if (n.leida) FontWeight.SemiBold else FontWeight.Bold, fontSize = 15.sp)
                Text(n.mensaje, fontSize = 13.sp, color = gris)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.Schedule, null, tint = gris, modifier = Modifier.size(14.dp))
                    Text(n.fecha.momento(), fontSize = 12.sp, color = gris)
                }
            }
            if (!n.leida) {
                Box(Modifier.padding(top = 6.dp).size(10.dp).clip(CircleShape).background(color))
            }
        }
    }
}

// Aviso emergente en la parte de arriba: aparece 4 segundos cuando llega una notificacion nueva
// (por ejemplo, cuando el otro perfil cambia un pedido). Solo reacciona a las que llegan despues
// de abrir la app: las que ya estaban no vuelven a salir
@Composable
fun AvisoEmergente(
    vm: NotificacionesViewModel,
    habilitado: Boolean,
    onAbrir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lista by vm.notificaciones.collectAsState()
    var ultimoId by remember { mutableStateOf<Int?>(null) }
    var visible by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<Notificacion?>(null) }

    LaunchedEffect(lista) {
        val actuales = lista ?: return@LaunchedEffect
        val maximo = actuales.maxOfOrNull { it.id } ?: 0
        val previo = ultimoId
        ultimoId = maximo
        if (previo != null && maximo > previo && habilitado) {
            // Llego una notificacion mientras se usa la app
            aviso = actuales.first { it.id == maximo }
            visible = true
        } else if (previo == null && habilitado) {
            // Al entrar: si hay avisos sin leer, se muestra el mas reciente despues de la bienvenida
            actuales.firstOrNull { !it.leida }?.let {
                delay(5300)
                aviso = it
                visible = true
            }
        }
    }
    LaunchedEffect(visible, aviso) {
        if (visible) {
            delay(4000)
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && aviso != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        val n = aviso ?: return@AnimatedVisibility
        val (icono, color) = estiloDe(TipoNotificacion.de(n.tipo))
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .clickable { visible = false; onAbrir() }
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) { Icon(icono, null, tint = color, modifier = Modifier.size(24.dp)) }
            Column(Modifier.weight(1f)) {
                Text(n.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(n.mensaje, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Text("Ahora", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
