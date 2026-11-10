package com.flores.mycar.ui.autos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.fichas.LogoMarcaRepository
import com.flores.mycar.ui.CargadorLogos
import com.flores.mycar.data.prefs.ComprasRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.ComprasEstado
import com.flores.mycar.domain.model.MarcaVenta
import com.flores.mycar.domain.model.Promocion
import com.flores.mycar.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel del catalogo de autos en venta (Inicio, Autos y Favoritos).
 * Se actualiza en tiempo real desde Firestore y conoce el carrito y los favoritos de la persona.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AutosViewModel(
    private val repo: AutoRepository,
    private val comprasRepo: ComprasRepository,
    logosRepo: LogoMarcaRepository? = null
) : ViewModel() {

    private val cargadorLogos = CargadorLogos(logosRepo, viewModelScope)

    /** Logo de cada marca (por nombre), a medida que se van encontrando. */
    val logos: StateFlow<Map<String, String>> = cargadorLogos.urls

    fun pedirLogo(marca: String) = cargadorLogos.pedir(marca)

    private val _estado = MutableStateFlow<UiState<List<Auto>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Auto>>> = _estado.asStateFlow()

    private val _secciones = MutableStateFlow<UiState<List<MarcaVenta>>>(UiState.Cargando)

    /** El catalogo agrupado por marca (pantalla Autos). */
    val secciones: StateFlow<UiState<List<MarcaVenta>>> = _secciones.asStateFlow()

    /** Promociones activas (se muestran en el inicio). */
    val promociones: StateFlow<List<Promocion>> = repo.promociones
        .map { lista -> lista.filter { it.activa } }
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val uid = MutableStateFlow<String?>(null)
    private var todasPedidas = false

    /** Carrito y favoritos de la persona que inicio sesion. */
    val compras: StateFlow<ComprasEstado> = uid
        .flatMapLatest { u -> if (u == null) flowOf(ComprasEstado()) else comprasRepo.estado(u) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ComprasEstado())

    init {
        observar()
        // Los autos del carrito y de favoritos necesitan que su marca este cargada.
        viewModelScope.launch { compras.collect { repo.asegurar(it.carrito + it.favoritos) } }
    }

    fun usuario(uid: String) {
        this.uid.value = uid
    }

    fun observar() {
        _estado.value = UiState.Cargando
        _secciones.value = UiState.Cargando
        viewModelScope.launch {
            repo.autos
                .catch { _estado.value = UiState.Error("No se pudieron cargar los autos") }
                .collect { _estado.value = UiState.Exito(it) }
        }
        viewModelScope.launch {
            repo.catalogo
                .catch { _secciones.value = UiState.Error("No se pudieron cargar los autos") }
                .collect { _secciones.value = UiState.Exito(it) }
        }
    }

    /** Pide a la API los autos de una marca cuando aparece en pantalla. */
    fun cargarMarca(idMarca: Int) {
        viewModelScope.launch { repo.cargarAutos(idMarca) }
    }

    /** Pide los autos de todas las marcas (para buscar un auto por su nombre). */
    fun cargarTodas() {
        if (todasPedidas) return
        todasPedidas = true
        val marcas = (_secciones.value as? UiState.Exito)?.datos.orEmpty()
        viewModelScope.launch { marcas.forEach { launch { repo.cargarAutos(it.id) } } }
    }

    fun alternarCarrito(autoId: String) {
        uid.value?.let { comprasRepo.alternarCarrito(it, autoId) }
    }

    fun alternarFavorito(autoId: String) {
        uid.value?.let { comprasRepo.alternarFavorito(it, autoId) }
    }
}
