package com.flores.mycar.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.domain.model.Promocion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** El administrador crea promociones (descuentos por mes), las activa, las desactiva o las borra. */
class PromocionesAdminViewModel(
    private val repo: AutoRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val promociones: StateFlow<List<Promocion>?> = repo.promociones
        .catch { _error.value = "No se pudieron cargar las promociones. Revisa las reglas de Firebase." }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun crear(promocion: Promocion) = ejecutar { repo.guardarPromocion(promocion) }

    fun alternar(promocion: Promocion, activa: Boolean) = ejecutar { repo.cambiarPromocion(promocion.id, activa) }

    fun eliminar(promocion: Promocion) = ejecutar { repo.eliminarPromocion(promocion.id) }

    /** Tres promociones listas para usar, por si el administrador quiere empezar rapido. */
    fun cargarEjemplos() = ejecutar {
        EJEMPLOS.forEach { repo.guardarPromocion(it) }
        Result.success(Unit)
    }

    fun errorMostrado() {
        _error.value = null
    }

    private fun ejecutar(accion: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            accion().onFailure { _error.value = "No se pudo guardar. Revisa las reglas de Firebase." }
        }
    }

    private companion object {
        val EJEMPLOS = listOf(
            Promocion(titulo = "Descuento del mes", descripcion = "En todos los autos", descuento = 10, mes = "Noviembre"),
            Promocion(titulo = "Semana Toyota", descripcion = "Corolla, Camry, Prius y más", descuento = 15, mes = "Diciembre", marca = "Toyota"),
            Promocion(titulo = "Eléctricos Tesla", descripcion = "Modelo S, 3, X y Y", descuento = 8, mes = "Enero", marca = "Tesla")
        )
    }
}
