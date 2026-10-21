package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.Descuento
import com.flores.lavacampus.model.LineaPedido
import com.flores.lavacampus.model.Unidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

private const val MAX_PEDIDOS_ACTIVOS = 3

// Reglas de "Nuevo pedido": cantidades, total, fecha de entrega y limite de pedidos
class NuevoPedidoViewModel(
    private val repo: LavaRepository,
    private val usuario: Usuario,
    prefs: PreferenciasStore
) : ViewModel() {

    // Si el administrador cerro la recepcion, el estudiante no puede crear pedidos
    val recibiendoPedidos: StateFlow<Boolean> = prefs.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Se muestran en el orden del catalogo (por id) y no alfabetico
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .map { lista -> lista.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // servicioId -> cantidad (si no esta en el mapa, el servicio esta apagado)
    private val _cantidades = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val cantidades: StateFlow<Map<Int, Double>> = _cantidades.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardando = MutableStateFlow(false)

    // subtotal = suma de cantidad x precio de cada servicio elegido
    val subtotal: StateFlow<Double> = combine(servicios, _cantidades) { lista, cant ->
        lista.sumOf { (cant[it.id] ?: 0.0) * it.precio }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Kilos de lavado elegidos (servicios cobrados por kilo)
    val kilosLavado: StateFlow<Double> = combine(servicios, _cantidades) { lista, cant ->
        lista.filter { it.unidad == Unidad.KG.name }.sumOf { cant[it.id] ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Descuento por volumen: 10 % del lavado si supera 5 kg (misma regla que guarda el Repository)
    val descuento: StateFlow<Double> = combine(servicios, _cantidades) { lista, cant ->
        val lavado = lista.filter { it.unidad == Unidad.KG.name }
        Descuento.sobreLavado(
            lavado.sumOf { cant[it.id] ?: 0.0 },
            lavado.sumOf { (cant[it.id] ?: 0.0) * it.precio }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // total = subtotal - descuento
    val total: StateFlow<Double> = combine(subtotal, descuento) { s, d -> s - d }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // hoy + mayor tiempo de entrega elegido (24 h = 1 dia); si cae domingo pasa al lunes
    val fechaEntrega: StateFlow<String?> = combine(servicios, _cantidades) { lista, cant ->
        val elegidos = lista.filter { (cant[it.id] ?: 0.0) > 0 }
        if (elegidos.isEmpty()) {
            null
        } else {
            var fecha = LocalDate.now().plusDays((elegidos.maxOf { it.horasEntrega } / 24).toLong())
            if (fecha.dayOfWeek == DayOfWeek.SUNDAY) fecha = fecha.plusDays(1)
            fecha.toString()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Boton habilitado solo si hay al menos un servicio con cantidad > 0
    val puedeConfirmar: StateFlow<Boolean> = combine(_cantidades, _guardando, recibiendoPedidos) { cant, guardando, recibe ->
        cant.values.any { it > 0 } && !guardando && recibe
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun cambiarNota(texto: String) {
        _nota.value = texto
    }

    // delta = +paso o -paso. KG: 0.5 en 0.5, minimo 1.0 y maximo 10.0. Otros: enteros hasta 30
    fun cambiarCantidad(servicioId: Int, delta: Double) {
        val servicio = servicios.value.firstOrNull { it.id == servicioId } ?: return
        val unidad = Unidad.de(servicio.unidad)
        val actual = _cantidades.value[servicioId] ?: 0.0
        var nueva = actual + delta
        when {
            actual == 0.0 && delta > 0 -> nueva = unidad.minimo // al activar arranca en el minimo
            nueva < unidad.minimo -> nueva = 0.0                // por debajo del minimo se apaga
            nueva > unidad.maximo -> nueva = unidad.maximo
        }
        _cantidades.value = if (nueva <= 0.0) _cantidades.value - servicioId
        else _cantidades.value + (servicioId to nueva)
        _mensaje.value = null
    }

    fun confirmar(onCreado: () -> Unit) {
        val lineas = _cantidades.value.filter { it.value > 0 }
            .map { LineaPedido(it.key, it.value) }
        val entrega = fechaEntrega.value
        if (lineas.isEmpty() || entrega == null) return

        viewModelScope.launch {
            _guardando.value = true
            if (repo.contarActivos(usuario.id) >= MAX_PEDIDOS_ACTIVOS) {
                _mensaje.value = "Ya tienes 3 pedidos activos"
            } else {
                try {
                    repo.crearPedido(usuario.id, lineas, entrega, _nota.value.trim())
                    onCreado()
                } catch (e: IllegalArgumentException) {
                    // El Repository valida cantidades y servicios: se muestra el motivo
                    _mensaje.value = e.message ?: "No se pudo crear el pedido"
                }
            }
            _guardando.value = false
        }
    }
}
