package com.flores.mycar.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.MarcaVenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** El administrador decide que marcas y que autos estan a la venta. Lo desactivado deja de verse para los usuarios. */
class CatalogoAdminViewModel(
    private val repo: AutoRepository,
    private val itemRepo: ItemRepository? = null
) : ViewModel() {

    val marcas: StateFlow<List<MarcaVenta>> = repo.catalogoCompleto
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val apagados: StateFlow<Set<String>> = repo.desactivados
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        // El admin puede entrar aqui sin haber abierto la lista de marcas: se descargan (Retrofit -> Room) si faltan.
        viewModelScope.launch { itemRepo?.refrescar() }
        // Se piden los autos de todas las marcas para poder activarlos o desactivarlos uno por uno.
        viewModelScope.launch {
            repo.catalogoCompleto.first { it.isNotEmpty() }.forEach { m -> launch { repo.cargarAutos(m.id) } }
        }
    }

    fun alternarMarca(marca: MarcaVenta, activa: Boolean) {
        viewModelScope.launch {
            repo.cambiarActivo("marca_${marca.id}", marca.nombre, activa)
                .onSuccess { _mensaje.value = if (activa) "${marca.nombre} vuelve a estar en venta" else "${marca.nombre} ya no está en venta" }
                .onFailure { _mensaje.value = "No se pudo cambiar. Revisa las reglas de Firebase." }
        }
    }

    fun alternarAuto(auto: Auto, activo: Boolean) {
        viewModelScope.launch {
            repo.cambiarActivo("auto_${auto.id}", auto.nombre, activo)
                .onSuccess { _mensaje.value = if (activo) "${auto.nombre} vuelve a estar en venta" else "${auto.nombre} ya no está en venta" }
                .onFailure { _mensaje.value = "No se pudo cambiar. Revisa las reglas de Firebase." }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }
}
