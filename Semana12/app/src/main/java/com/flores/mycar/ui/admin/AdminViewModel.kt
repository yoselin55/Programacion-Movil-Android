package com.flores.mycar.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.notification.Notificador
import com.flores.mycar.ui.mensajeDe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Datos que muestra el panel del administrador. */
data class AdminEstado(
    val totalGuardado: Int = 0,
    val totalFavoritos: Int = 0,
    val sincronizando: Boolean = false,
    val mensaje: String? = null
)

/** ViewModel del panel Admin: totales, sincronizar y limpiar cache. */
class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow(AdminEstado())
    val estado: StateFlow<AdminEstado> = _estado.asStateFlow()

    init {
        // Los totales salen de Room y se actualizan solos cuando cambian los datos.
        viewModelScope.launch {
            combine(repo.items, repo.favoritos) { todos, favoritos -> todos.size to favoritos.size }
                .collect { (todos, favoritos) ->
                    _estado.update { it.copy(totalGuardado = todos, totalFavoritos = favoritos) }
                }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _estado.update { it.copy(sincronizando = true, mensaje = null) }
            repo.refrescar()
                .onSuccess { n ->
                    _estado.update { it.copy(mensaje = "Sincronizado: $n marcas guardadas") }
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e -> _estado.update { it.copy(mensaje = mensajeDe(e)) } }
            _estado.update { it.copy(sincronizando = false) }
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repo.limpiarCache()
            _estado.update { it.copy(mensaje = "Caché limpiada") }
        }
    }
}
