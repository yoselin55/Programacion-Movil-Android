package com.flores.mycar.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.EstadoAuto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionEstado(
    val autos: List<Auto> = emptyList(),
    val citas: List<Cita> = emptyList(),
    val mensaje: String? = null
)

/** El admin cambia el estado de los pedidos (pago confirmado, entregado) y confirma las citas. */
class GestionViewModel(
    private val repo: AutoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(GestionEstado())
    val estado: StateFlow<GestionEstado> = _estado.asStateFlow()

    init {
        // Cada lista se carga por separado: si una falla (por ejemplo por reglas sin publicar), la otra se sigue viendo.
        viewModelScope.launch {
            repo.pedidos(null)
                .catch { _estado.update { e -> e.copy(mensaje = "No se pudieron cargar los pedidos. Revisa las reglas de Firestore.") } }
                .collect { lista -> _estado.update { e -> e.copy(autos = lista) } }
        }
        viewModelScope.launch {
            repo.citas(null)
                .catch { _estado.update { e -> e.copy(mensaje = "No se pudieron cargar las citas. Revisa las reglas de Firestore.") } }
                .collect { lista -> _estado.update { e -> e.copy(citas = lista) } }
        }
    }

    fun cambiarEstado(auto: Auto, nuevo: EstadoAuto) {
        viewModelScope.launch {
            repo.cambiarEstado(auto.id, nuevo)
                .onFailure { _estado.value = _estado.value.copy(mensaje = "No se pudo cambiar el estado") }
        }
    }

    fun confirmarCita(cita: Cita) {
        viewModelScope.launch {
            repo.confirmarCita(cita.id)
                .onFailure { _estado.value = _estado.value.copy(mensaje = "No se pudo confirmar la cita") }
        }
    }
}
