package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.ui.components.AppTopBar
import com.flores.lavacampus.ui.components.PedidoCard
import com.flores.lavacampus.ui.components.SelectorPestanas
import com.flores.lavacampus.ui.components.codigoPedido
import com.flores.lavacampus.viewmodel.MisPedidosViewModel

// La View solo muestra las listas del ViewModel y avisa los clics
@Composable
fun MisPedidosScreen(
    vm: MisPedidosViewModel,
    onVolver: () -> Unit,
    onVerPedido: (Int) -> Unit,
    onNuevoPedido: () -> Unit
) {
    val activos by vm.activos.collectAsState()
    val historial by vm.historial.collectAsState()
    val listo by vm.listoParaRecoger.collectAsState()
    var pestana by remember { mutableIntStateOf(0) }
    var porCancelar by remember { mutableStateOf<PedidoConDetalle?>(null) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Mis pedidos", onVolver)
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectorPestanas(listOf("Activos", "Historial"), pestana, { pestana = it })

                val lista = if (pestana == 0) activos else historial
                if (lista.isEmpty()) {
                    Text(
                        if (pestana == 0) "No tienes pedidos activos" else "Aún no tienes historial",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
                lista.forEach { pedido ->
                    PedidoCard(
                        pedido = pedido,
                        onClick = { onVerPedido(pedido.pedido.id) },
                        modoLista = true,
                        onCancelar = { porCancelar = pedido }
                    )
                }

                // Aviso cuando hay un pedido listo para recoger
                listo?.let { p ->
                    if (pestana == 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                .padding(14.dp)
                        ) {
                            Text("Aviso", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            Text(
                                "Tu pedido ${p.pedido.id.codigoPedido()} está listo: recógelo en el casillero indicado.",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Box(Modifier.padding(bottom = 80.dp))
            }

            FloatingActionButton(
                onClick = onNuevoPedido,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            ) { Icon(Icons.Filled.Add, contentDescription = "Nuevo pedido") }
        }
    }

    porCancelar?.let { pedido ->
        AlertDialog(
            onDismissRequest = { porCancelar = null },
            title = { Text("¿Cancelar el pedido ${pedido.pedido.id.codigoPedido()}?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { vm.cancelar(pedido); porCancelar = null }) { Text("Sí, cancelar") }
            },
            dismissButton = { TextButton(onClick = { porCancelar = null }) { Text("No") } }
        )
    }
}
