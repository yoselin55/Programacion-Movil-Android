package com.flores.lavacampus.viewmodel

import androidx.compose.ui.graphics.Color
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

// Un grupo de la bandeja (titulo, color del grupo y sus pedidos)
data class SeccionBandeja(val titulo: String, val color: Color, val pedidos: List<PedidoConDetalle>)

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

    // Los pedidos ordenados por urgencia y agrupados: primero lo que hay que atender ya.
    // Dentro de cada grupo, el que se debe entregar antes va arriba
    val secciones: StateFlow<List<SeccionBandeja>?> = combine(pedidos, atrasados) { lista, atrasados ->
        lista?.let { armarSecciones(it, atrasados) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun armarSecciones(lista: List<PedidoConDetalle>, atrasados: Set<Int>): List<SeccionBandeja> {
        fun estado(p: PedidoConDetalle) = EstadoPedido.valueOf(p.pedido.estado)
        val porEntrega = compareBy<PedidoConDetalle>({ it.pedido.fechaEntrega }, { it.pedido.id })
        val terminados = setOf(EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO)
        val (tarde, alDia) = lista.partition { it.pedido.id in atrasados }
        return listOf(
            SeccionBandeja("Atrasados", EstadoPedido.CANCELADO.color, tarde.sortedWith(porEntrega)),
            SeccionBandeja("Por iniciar", EstadoPedido.RECIBIDO.color, alDia.filter { estado(it) == EstadoPedido.RECIBIDO }.sortedWith(porEntrega)),
            SeccionBandeja("En proceso", EstadoPedido.EN_PROCESO.color, alDia.filter { estado(it) == EstadoPedido.EN_PROCESO }.sortedWith(porEntrega)),
            SeccionBandeja("Listos para recoger", EstadoPedido.LISTO.color, alDia.filter { estado(it) == EstadoPedido.LISTO }.sortedWith(porEntrega)),
            SeccionBandeja("Terminados", EstadoPedido.ENTREGADO.color, alDia.filter { estado(it) in terminados }.sortedByDescending { it.pedido.id })
        ).filter { it.pedidos.isNotEmpty() }
    }

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
