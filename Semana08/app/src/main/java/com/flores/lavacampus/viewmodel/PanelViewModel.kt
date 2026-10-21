package com.flores.lavacampus.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.DatoGrafico
import com.flores.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

// Cifras del panel: se actualizan solas cuando cambian los pedidos
class PanelViewModel(repo: LavaRepository) : ViewModel() {

    private val hoy = LocalDate.now().toString()
    // Lunes de esta semana (si hoy es lunes, es hoy; si es domingo, el lunes anterior)
    private val inicioSemana = LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()

    // Convierte un Flow de Room en StateFlow para la pantalla
    private fun <T> Flow<T>.estado(inicial: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), inicial)

    val pedidosHoy: StateFlow<Int> = repo.observarPedidosDeFecha(hoy).estado(0)
    val enProceso: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO).estado(0)
    val porRecoger: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.LISTO).estado(0)

    // Pedidos cuya entrega ya paso y siguen sin entregarse
    val atrasados: StateFlow<Int> = repo.observarPedidosAtrasados(hoy).map { it.size }.estado(0)

    // Suma del total de los pedidos pagados desde el lunes
    val ingresosSemana: StateFlow<Double> = repo.observarIngresosDesde(inicioSemana).estado(0.0)

    // Dona: pedidos por estado (se combinan 4 consultas)
    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        repo.observarCantidadPorEstado(EstadoPedido.RECIBIDO),
        repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO),
        repo.observarCantidadPorEstado(EstadoPedido.LISTO),
        repo.observarCantidadPorEstado(EstadoPedido.ENTREGADO)
    ) { recibido, proceso, listo, entregado ->
        listOf(
            DatoGrafico(EstadoPedido.RECIBIDO.texto, recibido.toFloat(), EstadoPedido.RECIBIDO.color),
            DatoGrafico(EstadoPedido.EN_PROCESO.texto, proceso.toFloat(), EstadoPedido.EN_PROCESO.color),
            DatoGrafico(EstadoPedido.LISTO.texto, listo.toFloat(), EstadoPedido.LISTO.color),
            DatoGrafico(EstadoPedido.ENTREGADO.texto, entregado.toFloat(), EstadoPedido.ENTREGADO.color)
        )
    }.estado(emptyList())

    // Cantidad de pedidos pagados desde el lunes (se muestra junto a los ingresos)
    val pedidosPagadosSemana: StateFlow<Int> = repo.observarPedidos()
        .map { lista -> lista.count { it.pedido.pagado && it.pedido.fechaRegistro >= inicioSemana } }
        .estado(0)

    // Barras: ingresos por servicio (solo pedidos pagados de la semana), de mayor a menor.
    // La barra mas alta va en violeta fuerte y las demas en violeta claro
    val porServicio: StateFlow<List<DatoGrafico>> = repo.observarIngresosPorServicio(inicioSemana)
        .map { lista ->
            lista.sortedByDescending { it.total }.mapIndexed { i, ingreso ->
                DatoGrafico(
                    etiquetaCorta(ingreso.nombre),
                    ingreso.total.toFloat(),
                    if (i == 0) Color(0xFF6D28D9) else Color(0xFFC4B5FD)
                )
            }
        }
        .estado(emptyList())

    // "Lavado y secado" -> "Lavado", "Lavado en seco" -> "Seco"
    private fun etiquetaCorta(nombre: String) = when {
        nombre.startsWith("Lavado en seco", ignoreCase = true) -> "Seco"
        nombre.startsWith("Lavado", ignoreCase = true) -> "Lavado"
        else -> nombre
    }
}
