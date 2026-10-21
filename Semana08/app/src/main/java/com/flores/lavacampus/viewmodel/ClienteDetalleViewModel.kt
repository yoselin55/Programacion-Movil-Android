package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.ClienteResumen
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

// Lo que muestra la ficha de un cliente
data class ClienteDetalleUi(
    val resumen: ClienteResumen,
    val correo: String,
    val activos: Int,
    val pedidos: List<PedidoConDetalle>
)

// Ficha de un cliente: sus datos, sus cifras y todos sus pedidos
class ClienteDetalleViewModel(repo: LavaRepository, clienteId: Int) : ViewModel() {

    private val correo = flow { emit(repo.obtenerUsuario(clienteId)?.correo ?: "") }

    // null = todavia cargando
    val ui: StateFlow<ClienteDetalleUi?> = combine(
        repo.observarClientes(),
        repo.observarPedidosDeUsuario(clienteId),
        correo
    ) { clientes, pedidos, correo ->
        clientes.firstOrNull { it.id == clienteId }?.let { resumen ->
            val finalizados = setOf(EstadoPedido.ENTREGADO.name, EstadoPedido.CANCELADO.name)
            ClienteDetalleUi(
                resumen = resumen,
                correo = correo,
                activos = pedidos.count { it.pedido.estado !in finalizados },
                pedidos = pedidos
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
