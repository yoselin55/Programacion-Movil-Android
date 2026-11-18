package com.flores.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.centrotareas.data.TareasRepository
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Commit 1: esqueleto del ViewModel; cada paso lo completa en su propio commit
class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (pasos 1 a 4)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Commit 4 (Paso 3): progreso de 0f a 1f (solo lectura hacia afuera)
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    // Commit 3: la tarea larga pasa a una corrutina con withContext
    fun tareaLarga() {
        viewModelScope.launch {
            _mensaje.value = "Trabajando..."
            _progreso.value = 0f      // Commit 4
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            withContext(Dispatchers.Default) {
                Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                for (i in 1..100) {
                    delay(40)               // 100 vueltas x 40 ms = 4 s
                    _progreso.value = i / 100f   // Commit 4
                }
            }
            Log.d("HILO", "Despues: ${Thread.currentThread().name}")
            _mensaje.value = "Terminó"
        }
    }
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
