package com.flores.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import com.flores.centrotareas.data.TareasRepository
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Commit 1: esqueleto del ViewModel; cada paso lo completa en su propio commit
class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (pasos 1 a 4)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Commit 2 (Paso 1): tarea larga MAL hecha, en el hilo principal (congela la pantalla)
    fun tareaLargaMala() {
        _mensaje.value = "Trabajando..."
        Thread.sleep(4000)
        _mensaje.value = "Terminó"
    }
    // TODO Commit 3 (Paso 2): fun tareaLarga()
    // TODO Commit 4 (Paso 3): progreso
    // TODO Commit 5 (Paso 4): trabajo (Job), corriendo y cancelar()

    // ----- Contar primos (paso 5)
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    // TODO Commit 6 (Paso 5): fun contar(hasta: Int) y fun cancelarPrimos()

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    // TODO Commit 7 (Paso 6): fun cargarUnaPorUna() y fun cargarALaVez()
    // TODO Commit 8 (Paso 7): simularError y manejo de errores
}
