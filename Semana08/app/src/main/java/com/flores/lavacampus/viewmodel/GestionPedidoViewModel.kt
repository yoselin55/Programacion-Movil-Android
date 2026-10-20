package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.relation.PedidoConDetalle
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.Casilleros
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Gestion de un pedido: aqui viven las reglas de la maquina de estados
class GestionPedidoViewModel(
    private val repo: LavaRepository,
    private val pedidoId: Int
) : ViewModel() {

    val pedido: StateFlow<PedidoConDetalle?> = repo.observarPedido(pedidoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _elegido = MutableStateFlow<String?>(null)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Casilleros usados por otros pedidos LISTO. El casillero del propio pedido no cuenta como ocupado
    val casillerosOcupados: StateFlow<Set<String>> = combine(
        repo.observarCasillerosOcupados(), pedido
    ) { ocupados, p ->
        ocupados.toSet() - setOfNotNull(p?.pedido?.casillero)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Casillero elegido en pantalla; si no se eligio ninguno, el que ya tiene el pedido
    val casillero: StateFlow<String?> = combine(_elegido, pedido) { elegido, p ->
        elegido ?: p?.pedido?.casillero
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun elegirCasillero(c: String) {
        if (c in casillerosOcupados.value) return
        _elegido.value = c
        _mensaje.value = null
    }

    fun marcarPagado(pagado: Boolean) {
        viewModelScope.launch { repo.marcarPagado(pedidoId, pagado) }
        _mensaje.value = null
    }

    // Solo al estado siguiente. LISTO exige casillero libre; ENTREGADO exige pedido pagado
    fun avanzar() {
        val p = pedido.value?.pedido ?: return
        val siguiente = EstadoPedido.valueOf(p.estado).siguiente()
        if (siguiente == null) {
            _mensaje.value = "Este pedido ya está finalizado"
            return
        }
        var casilleroFinal = p.casillero
        when (siguiente) {
            EstadoPedido.LISTO -> {
                val c = casillero.value
                when {
                    c == null -> return fallo("Elige un casillero para pasar a Listo")
                    !Casilleros.esValido(c) -> return fallo("El casillero debe ser de C-01 a C-30")
                    c in casillerosOcupados.value -> return fallo("El casillero $c ya está ocupado")
                }
                casilleroFinal = c
            }
            EstadoPedido.ENTREGADO ->
                if (!p.pagado) return fallo("Marca el pedido como Cobrado antes de entregarlo")
            else -> Unit
        }
        viewModelScope.launch {
            repo.cambiarEstado(pedidoId, siguiente, casilleroFinal)
            _elegido.value = null
            _mensaje.value = null
        }
    }

    // Solo desde RECIBIDO o EN_PROCESO
    fun cancelar() {
        val p = pedido.value?.pedido ?: return
        val estado = EstadoPedido.valueOf(p.estado)
        if (estado != EstadoPedido.RECIBIDO && estado != EstadoPedido.EN_PROCESO) {
            _mensaje.value = "No se puede cancelar un pedido ${estado.texto.lowercase()}"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, null)
            _mensaje.value = null
        }
    }

    private fun fallo(texto: String) {
        _mensaje.value = texto
    }
}
