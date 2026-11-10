package com.flores.mycar.ui.autos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.prefs.ComprasRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.ComprasEstado
import com.flores.mycar.domain.model.FichaAuto
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado de la ficha del auto: buscando, encontrada (o no hay nada que mostrar), y los modelos de la marca. */
data class FichaEstado(
    val cargando: Boolean = true,
    val ficha: FichaAuto? = null,
    val modelos: List<String> = emptyList()
)

/** Detalle de un auto: galeria, datos, descripcion, color, carrito, favorito y compra. */
@OptIn(ExperimentalCoroutinesApi::class)
class AutoDetalleViewModel(
    private val repo: AutoRepository,
    private val comprasRepo: ComprasRepository,
    private val autoId: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Auto>>(UiState.Cargando)
    val estado: StateFlow<UiState<Auto>> = _estado.asStateFlow()

    private val _ficha = MutableStateFlow(FichaEstado())
    val ficha: StateFlow<FichaEstado> = _ficha.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val uid = MutableStateFlow<String?>(null)
    val compras: StateFlow<ComprasEstado> = uid
        .flatMapLatest { u -> if (u == null) flowOf(ComprasEstado()) else comprasRepo.estado(u) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ComprasEstado())

    private var fichaPedida = false

    init {
        viewModelScope.launch { repo.asegurar(listOf(autoId)) }
        viewModelScope.launch {
            repo.autos
                .catch { _estado.value = UiState.Error("No se pudo cargar el auto") }
                .collect { lista ->
                    val auto = lista.firstOrNull { it.id == autoId }
                    _estado.value = if (auto != null) UiState.Exito(auto) else UiState.Error("Este auto no se encontró")
                    if (auto != null) cargarFicha(auto)
                }
        }
    }

    fun usuario(uid: String) {
        this.uid.value = uid
    }

    /** Busca los modelos, la descripcion y mas fotos de la marca, una sola vez. */
    private fun cargarFicha(auto: Auto) {
        if (fichaPedida) return
        fichaPedida = true
        viewModelScope.launch {
            val modelos = repo.modelos(auto)
            _ficha.value = FichaEstado(cargando = true, modelos = modelos)
            _ficha.value = FichaEstado(cargando = false, ficha = repo.ficha(auto), modelos = modelos)
        }
    }

    fun alternarCarrito() {
        val u = uid.value ?: return
        val estabaEnCarrito = autoId in compras.value.carrito
        comprasRepo.alternarCarrito(u, autoId)
        _mensaje.value = if (estabaEnCarrito) "Quitado del carrito" else "Agregado al carrito"
    }

    fun alternarFavorito() {
        uid.value?.let { comprasRepo.alternarFavorito(it, autoId) }
    }

    /** Compra: queda un pedido "en proceso" a nombre de la persona. */
    fun comprar(sesion: Sesion, auto: Auto, color: String, recojo: String, direccion: String) {
        viewModelScope.launch {
            repo.comprar(auto, color, sesion.uid, sesion.correo, recojo, direccion)
                .onSuccess {
                    comprasRepo.quitarDelCarrito(sesion.uid, autoId)
                    _mensaje.value = "Pedido en proceso. Sigue su estado en «Mis pedidos»."
                }
                .onFailure { _mensaje.value = "No se pudo registrar la compra. Inténtalo de nuevo." }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
