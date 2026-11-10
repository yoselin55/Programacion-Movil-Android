package com.flores.mycar.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.mensajeDe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel del detalle: pide al repositorio los datos del segundo endpoint. */
class DetalleViewModel(
    private val repo: ItemRepository,
    private val autoRepo: AutoRepository,
    private val id: Int
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado: StateFlow<UiState<Item>> = _estado.asStateFlow()

    /** Los autos en venta de esta marca (los que tienen foto propia en el catalogo). */
    val enVenta: StateFlow<List<Auto>> = autoRepo.autos
        .map { lista -> lista.filter { it.id.substringBefore('-').toIntOrNull() == id } }
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        cargar()
        // Los autos de esta marca (hasta 8) se piden a la API.
        viewModelScope.launch { autoRepo.cargarAutos(id) }
    }

    /** Marca o desmarca la marca como favorita y refleja el cambio en pantalla. */
    fun alternarFavorito() {
        viewModelScope.launch {
            repo.alternarFavorito(id)
            val actual = _estado.value
            if (actual is UiState.Exito) {
                _estado.value = UiState.Exito(actual.datos.copy(esFavorito = !actual.datos.esFavorito))
            }
        }
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repo.obtenerDetalle(id)
                .onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error(mensajeDe(it)) }
        }
    }
}
