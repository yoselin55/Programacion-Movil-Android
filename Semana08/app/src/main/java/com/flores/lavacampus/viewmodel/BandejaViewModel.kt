package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// Bandeja del administrador: todos los pedidos con filtro por estado y buscador
class BandejaViewModel(private val repo: LavaRepository) : ViewModel() {

    // null = Todos
    private val _filtro = MutableStateFlow<EstadoPedido?>(null)
    val filtro: StateFlow<EstadoPedido?> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Se combinan los pedidos, el chip elegido y el texto del buscador
    // null = todavia cargando (no se muestra "no hay pedidos" hasta saber si de verdad no hay)
    val pedidos: StateFlow<List<PedidoConDetalle>?> = combine(
        repo.observarPedidos(), _filtro, _busqueda
    ) { lista, filtro, texto ->
        val q = texto.trim().removePrefix("#").lowercase()
        lista.filter { p ->
            (filtro == null || p.pedido.estado == filtro.name) &&
                (q.isEmpty() ||
                    p.usuario.nombre.lowercase().contains(q) ||
                    p.pedido.id.toString().padStart(3, '0').contains(q) ||
                    p.pedido.id.toString() == q)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Ids de los pedidos atrasados: la tarjeta se marca en rojo
    val atrasados: StateFlow<Set<Int>> = repo.observarPedidosAtrasados(LocalDate.now().toString())
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun elegirFiltro(estado: EstadoPedido?) {
        _filtro.value = estado
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    // "Iniciar": RECIBIDO -> EN_PROCESO no pide nada mas.
    // Pasar a LISTO o ENTREGADO exige casillero y cobro, y se hace en la pantalla de gestion
    fun iniciar(pedido: PedidoConDetalle) {
        if (pedido.pedido.estado != EstadoPedido.RECIBIDO.name) return
        viewModelScope.launch {
            repo.cambiarEstado(pedido.pedido.id, EstadoPedido.EN_PROCESO, null)
        }
    }
}
