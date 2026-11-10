package com.flores.mycar.ui.autos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.AutoRepository
import com.flores.mycar.domain.model.Auto
import com.flores.mycar.domain.model.Cita
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.domain.model.claveHorario
import com.flores.mycar.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Formulario para agendar una cita y ver un auto en persona. */
class AgendarCitaViewModel(
    private val repo: AutoRepository,
    private val autoId: String
) : ViewModel() {

    private val _auto = MutableStateFlow<UiState<Auto>>(UiState.Cargando)
    val auto: StateFlow<UiState<Auto>> = _auto.asStateFlow()

    /** Horarios ya tomados por cualquier persona. */
    val ocupados: StateFlow<Set<String>> = repo.horariosOcupados
        .catch { emit(emptySet()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _enviando = MutableStateFlow(false)
    val enviando: StateFlow<Boolean> = _enviando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** La cita agendada (cuando sale bien). */
    private val _exito = MutableStateFlow<Cita?>(null)
    val exito: StateFlow<Cita?> = _exito.asStateFlow()

    init {
        viewModelScope.launch { repo.asegurar(listOf(autoId)) }
        viewModelScope.launch {
            repo.autos
                .catch { _auto.value = UiState.Error("No se pudo cargar el auto") }
                .collect { lista ->
                    val a = lista.firstOrNull { it.id == autoId }
                    _auto.value = if (a != null) UiState.Exito(a) else UiState.Error("Este auto no se encontró")
                }
        }
    }

    fun agendar(sesion: Sesion, auto: Auto, fecha: String, hora: String, nota: String) {
        if (fecha.isBlank() || hora.isBlank()) {
            _error.value = "Elige el día y la hora"
            return
        }
        if (claveHorario(fecha, hora) in ocupados.value) {
            _error.value = "Ese horario ya está ocupado. Elige otro."
            return
        }
        viewModelScope.launch {
            _enviando.value = true
            _error.value = null
            val cita = Cita(
                autoId = auto.id,
                autoNombre = auto.nombre,
                uid = sesion.uid,
                correo = sesion.correo,
                fecha = fecha,
                hora = hora,
                nombre = sesion.nombre,
                nota = nota.trim()
            )
            repo.agendarCita(cita)
                .onSuccess { _exito.value = cita }
                .onFailure { _error.value = "No se pudo agendar: puede que ese horario ya esté ocupado. Elige otro." }
            _enviando.value = false
        }
    }
}
