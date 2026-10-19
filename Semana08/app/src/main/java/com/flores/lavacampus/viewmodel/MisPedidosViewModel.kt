package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Lista de pedidos del estudiante dividida en Activos e Historial
class MisPedidosViewModel(
    private val repo: LavaRepository,
    usuario: Usuario
) : ViewModel() {

    private val pedidos = repo.observarPedidosDeUsuario(usuario.id)

    // Activos = RECIBIDO, EN_PROCESO y LISTO
    val activos: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { lista -> lista.filter { estadoDe(it) in ACTIVOS } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Historial = ENTREGADO y CANCELADO
    val historial: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { lista -> lista.filter { estadoDe(it) !in ACTIVOS } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Primer pedido LISTO: sirve para el aviso "recogelo en el casillero"
    val listoParaRecoger: StateFlow<PedidoConDetalle?> = activos
        .map { lista -> lista.firstOrNull { estadoDe(it) == EstadoPedido.LISTO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Misma regla del Detalle: solo se cancela en RECIBIDO
    fun cancelar(pedido: PedidoConDetalle) {
        if (estadoDe(pedido) != EstadoPedido.RECIBIDO) return
        viewModelScope.launch {
            repo.cambiarEstado(pedido.pedido.id, EstadoPedido.CANCELADO, null)
        }
    }

    private fun estadoDe(p: PedidoConDetalle) = EstadoPedido.valueOf(p.pedido.estado)

    private companion object {
        val ACTIVOS = listOf(EstadoPedido.RECIBIDO, EstadoPedido.EN_PROCESO, EstadoPedido.LISTO)
    }
}
