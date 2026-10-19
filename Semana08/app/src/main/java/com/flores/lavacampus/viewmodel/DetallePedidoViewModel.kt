package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Detalle de un pedido: se actualiza solo cuando el administrador cambia el estado
class DetallePedidoViewModel(
    private val repo: LavaRepository,
    private val pedidoId: Int
) : ViewModel() {

    val pedido: StateFlow<PedidoConDetalle?> = repo.observarPedido(pedidoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Regla: el estudiante solo cancela si el pedido sigue en RECIBIDO
    val puedeCancelar: StateFlow<Boolean> = pedido
        .map { it?.pedido?.estado == EstadoPedido.RECIBIDO.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun cancelar() {
        // Se vuelve a validar aqui: no se confia solo en el boton de la pantalla
        if (!puedeCancelar.value) return
        viewModelScope.launch {
            repo.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, null)
        }
    }
}
