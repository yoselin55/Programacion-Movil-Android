package com.flores.lavacampus.ui.administrador

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.BannerPantalla
import com.flores.lavacampus.ui.components.EstadoVacio
import com.flores.lavacampus.ui.components.PedidoCard
import com.flores.lavacampus.ui.components.iniciales
import com.flores.lavacampus.ui.components.soles
import com.flores.lavacampus.viewmodel.ClienteDetalleViewModel

// Ficha de un cliente: sus datos, tres cifras y todos sus pedidos (al tocar uno se gestiona)
@Composable
fun ClienteDetalleScreen(vm: ClienteDetalleViewModel, onVolver: () -> Unit, onAbrirPedido: (Int) -> Unit) {
    val ui by vm.ui.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Cliente", onVolver)
        // Hasta que Room responde solo se ve la barra: nunca una ficha vacia por error
        val d = ui ?: return@Column

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BannerPantalla(
                R.drawable.foto_clientes,
                d.resumen.nombre,
                if (d.correo.isEmpty()) d.resumen.codigo else "${d.resumen.codigo} · ${d.correo}",
                alto = 170.dp,
                alineacion = BiasAlignment(0f, 0.85f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        d.resumen.nombre.iniciales(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Cifra(d.resumen.cantidadPedidos.toString(), "Pedidos", Modifier.weight(1f))
                Cifra(d.activos.toString(), "Activos", Modifier.weight(1f))
                Cifra(d.resumen.totalGastado.soles(), "Gastado", Modifier.weight(1f))
            }

            Text(
                "Pedidos de ${d.resumen.nombre.substringBefore(" ")}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            if (d.pedidos.isEmpty()) {
                EstadoVacio("Aún no tiene pedidos", "Cuando haga uno, aparecerá aquí")
            }
            d.pedidos.forEach { p ->
                PedidoCard(p, onClick = { onAbrirPedido(p.pedido.id) }, modoLista = true)
            }
        }
    }
}

@Composable
private fun Cifra(valor: String, titulo: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
        Text(titulo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
    }
}
