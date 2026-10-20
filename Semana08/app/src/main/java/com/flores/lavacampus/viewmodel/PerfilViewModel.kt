package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Contadores del perfil del estudiante
class PerfilViewModel(repo: LavaRepository, val usuario: Usuario) : ViewModel() {

    private val pedidos = repo.observarPedidosDeUsuario(usuario.id)

    val pedidosActivos: StateFlow<Int> = pedidos
        .map { lista ->
            lista.count {
                EstadoPedido.valueOf(it.pedido.estado) in
                    listOf(EstadoPedido.RECIBIDO, EstadoPedido.EN_PROCESO, EstadoPedido.LISTO)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pedidosTotales: StateFlow<Int> = pedidos
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Kilos de lavado de los pedidos ya entregados
    val kilosLavados: StateFlow<Double> = pedidos
        .map { lista ->
            lista.filter { it.pedido.estado == EstadoPedido.ENTREGADO.name }
                .flatMap { it.detalles }
                .filter { it.servicio.unidad == "KG" }
                .sumOf { it.detalle.cantidad }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}
