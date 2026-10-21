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

// Todo lo que muestra Inicio llega junto. Mientras Room no responde el estado es null ("cargando"),
// asi la pantalla no dice "no tienes pedidos" por un instante antes de mostrar los reales
data class InicioUi(
    val activo: PedidoConDetalle?,
    val recientes: List<PedidoConDetalle>
)

// Datos de la pantalla Inicio del estudiante
class InicioViewModel(
    repo: LavaRepository,
    val usuario: Usuario
) : ViewModel() {

    private val pedidos = repo.observarPedidosDeUsuario(usuario.id)

    // Pedido activo = el mas reciente en RECIBIDO, EN_PROCESO o LISTO
    // (la lista llega ordenada del mas nuevo al mas antiguo)
    val pedidoActivo: StateFlow<PedidoConDetalle?> = pedidos
        .map { lista ->
            lista.firstOrNull {
                EstadoPedido.valueOf(it.pedido.estado) in
                    listOf(EstadoPedido.RECIBIDO, EstadoPedido.EN_PROCESO, EstadoPedido.LISTO)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Estado unico de la pantalla (null = cargando)
    val ui: StateFlow<InicioUi?> = pedidos
        .map { lista ->
            InicioUi(
                activo = lista.firstOrNull {
                    EstadoPedido.valueOf(it.pedido.estado) in
                        listOf(EstadoPedido.RECIBIDO, EstadoPedido.EN_PROCESO, EstadoPedido.LISTO)
                },
                recientes = lista.take(2)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Los 2 ultimos pedidos
    val recientes: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { it.take(2) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
