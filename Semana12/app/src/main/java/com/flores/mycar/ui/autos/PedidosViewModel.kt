package com.flores.mycar.ui.autos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PedidosEstado(
    val compras: List<Auto> = emptyList(),
    val citas: List<Cita> = emptyList(),
    val error: String? = null
)

/** "Mis pedidos": los autos que la persona pago (de prueba) y sus citas. */
class PedidosViewModel(
    private val repo: AutoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PedidosEstado())
    val estado: StateFlow<PedidosEstado> = _estado.asStateFlow()

    private var trabajo: Job? = null

    /** Empieza a observar los pedidos de esa persona. Si ya los observa, no repite. */
    fun observar(uid: String) {
        if (trabajo?.isActive == true) return
        trabajo = viewModelScope.launch {
            combine(repo.pedidos(uid), repo.citas(uid)) { pedidos, citas ->
                PedidosEstado(compras = pedidos, citas = citas)
            }
                .catch { _estado.value = PedidosEstado(error = "No se pudieron cargar tus pedidos") }
                .collect { _estado.value = it }
        }
    }
}
