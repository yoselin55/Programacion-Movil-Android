package com.flores.mycar.ui.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.prefs.ComprasRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.EstadoAuto
import com.flores.mycar.domain.model.Sesion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CarritoEstado(
    val items: List<Auto> = emptyList(),
    val total: Double = 0.0,
    val comprando: Boolean = false,
    val mensaje: String? = null
)

/** Carrito de compras: los autos que la persona guardo para comprar. La compra es de prueba. */
@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModel(
    private val autoRepo: AutoRepository,
    private val comprasRepo: ComprasRepository
) : ViewModel() {

    private val uid = MutableStateFlow<String?>(null)
    private val extra = MutableStateFlow(false to (null as String?))   // comprando, mensaje

    val estado: StateFlow<CarritoEstado> = combine(
        uid.flatMapLatest { u -> if (u == null) flowOf(emptySet()) else comprasRepo.estado(u).map { it.carrito } },
        autoRepo.autos.catch { emit(emptyList()) },
        extra
    ) { ids, autos, (comprando, mensaje) ->
        val items = autos.filter { it.id in ids }
        CarritoEstado(
            items = items,
            total = items.filter { it.estado == EstadoAuto.DISPONIBLE }.sumOf { it.precio },
            comprando = comprando,
            mensaje = mensaje
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CarritoEstado())

    fun usuario(uid: String) {
        this.uid.value = uid
        // Los autos del carrito necesitan que su marca este cargada.
        viewModelScope.launch { comprasRepo.estado(uid).collect { autoRepo.asegurar(it.carrito) } }
    }

    fun quitar(autoId: String) {
        uid.value?.let { comprasRepo.quitarDelCarrito(it, autoId) }
    }

    /** Compra todos los autos disponibles del carrito. */
    fun comprar(sesion: Sesion, recojo: String = "", direccion: String = "") {
        viewModelScope.launch {
            extra.value = true to null
            val disponibles = estado.value.items.filter { it.estado == EstadoAuto.DISPONIBLE }
            var bien = 0
            var mal = 0
            disponibles.forEach { auto ->
                autoRepo.comprar(auto, "", sesion.uid, sesion.correo, recojo, direccion)
                    .onSuccess { comprasRepo.quitarDelCarrito(sesion.uid, auto.id); bien++ }
                    .onFailure { mal++ }
            }
            extra.value = false to when {
                bien > 0 && mal == 0 -> "Pedido en proceso ($bien). Sigue su estado en «Mis pedidos»."
                bien > 0 -> "Se compraron $bien; $mal no se pudieron comprar."
                else -> "No se pudo registrar la compra. Inténtalo de nuevo."
            }
        }
    }

    fun mensajeMostrado() {
        extra.value = extra.value.first to null
    }
}
