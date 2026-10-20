package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Gestion de servicios del administrador (Flow -> StateFlow con stateIn)
class ServiciosViewModel(private val repo: LavaRepository) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repo.observarServicios()
        .map { lista -> lista.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun alternar(s: Servicio) {
        viewModelScope.launch { repo.cambiarActivo(s.id, !s.activo) }
    }

    // Nuevo (id = 0) o editado. Si los datos no son validos se muestra el motivo
    fun guardar(servicio: Servicio, onListo: () -> Unit) {
        when {
            servicio.nombre.isBlank() -> _mensaje.value = "El nombre es obligatorio"
            servicio.precio <= 0.0 -> _mensaje.value = "El precio debe ser mayor que 0"
            else -> viewModelScope.launch {
                repo.guardarServicio(servicio.copy(nombre = servicio.nombre.trim()))
                _mensaje.value = null
                onListo()
            }
        }
    }

    // Un servicio que ya esta en algun pedido no se elimina: solo se desactiva
    fun eliminar(servicio: Servicio, onListo: () -> Unit) {
        viewModelScope.launch {
            if (repo.servicioTieneDetalles(servicio.id)) {
                _mensaje.value = "No se puede eliminar: ya está en pedidos. Solo puedes desactivarlo."
            } else {
                repo.eliminarServicio(servicio)
                _mensaje.value = null
                onListo()
            }
        }
    }
}
