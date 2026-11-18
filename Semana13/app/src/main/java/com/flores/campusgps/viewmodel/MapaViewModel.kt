package com.flores.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.campusgps.data.LugaresRepository
import com.flores.campusgps.data.UbicacionRepository
import com.flores.campusgps.model.Lugar
import com.flores.campusgps.model.LugarConDistancia
import com.flores.campusgps.data.GpsApagadoException
import com.flores.campusgps.model.Posicion
import com.flores.campusgps.model.TipoError
import com.flores.campusgps.model.UbicacionUi
import com.flores.campusgps.util.distanciaMetros
import com.flores.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Commit 1: ViewModel con el estado de la ubicación; los TODO se completan en los commits 6 al 9.
class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    fun buscar() {
        // Commit 9: antes de buscar se consulta si hay Google Play Services (sin usar Context).
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it) // Commit 6: solo se calcula cuando la ubicación llega con éxito
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    // Commit 6: calcula la distancia a cada lugar, ordena de menor a mayor y publica la lista.
    // Se hace aquí en el ViewModel y no en un Composable, para respetar MVVM.
    private fun actualizarDistancias(pos: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { LugarConDistancia(it, distanciaMetros(pos, it)) }
            .sortedBy { it.metros }
        // Commit 6: para comprobar el orden en Logcat (filtrar por "CampusGPS").
        Log.d("CampusGPS", _lugares.value.joinToString { "${it.lugar.nombre}=${it.metros?.let(::formatearDistancia)}" })
    }

    // Commit 7: guarda el lugar seleccionado; el mapa y la lista reaccionan a este valor.
    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }
    // Commit 8: el lugar más cercano se deriva de la lista ordenada (el primero con distancia calculada).
    // Es null mientras todavía no hay ubicación.
    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Commit 9: convierte cada fallo en un error con su mensaje y su tipo.
    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error("Falta el permiso de ubicación", TipoError.SIN_PERMISO)
        is GpsApagadoException -> UbicacionUi.Error("Activa el GPS e intenta de nuevo", TipoError.GPS_APAGADO)
        else -> UbicacionUi.Error(e.message ?: "No se pudo obtener la ubicación", TipoError.OTRO)
    }
}
