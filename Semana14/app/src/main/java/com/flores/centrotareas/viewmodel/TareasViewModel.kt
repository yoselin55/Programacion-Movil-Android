package com.flores.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.centrotareas.data.TareasRepository
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    // Commit 5 (Paso 4): guardamos el Job para poder cancelarlo y evitar dos tareas a la vez
    private var trabajo: Job? = null
    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    // Commit 3: la tarea larga pasa a una corrutina con withContext
    fun tareaLarga() {
        if (_corriendo.value) return          // Commit 5: ya hay una tarea corriendo
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            try {
                _mensaje.value = "Trabajando..."
                _progreso.value = 0f      // Commit 4
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f   // Commit 4
                    }
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false      // termine bien o cancelada
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos (paso 5)
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    // Commit 6 (Paso 5): el cálculo vive en el Repository; aquí solo se lanza, se mide y se cancela
    private var trabajoPrimos: Job? = null

    fun contar(hasta: Int) {
        if (_primos.value is PrimosUi.Calculando) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta)
                _primos.value = PrimosUi.Listo(cantidad, System.currentTimeMillis() - inicio)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e                       // nunca tragarse la cancelación
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    // Commit 7 (Paso 6): una por una (secuencial) contra a la vez (paralelo con async)
    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            val perfil = repo.cargarPerfil()               // 2 s
            val pedidos = repo.cargarPedidos(false)        // + 3 s = 5 s
            _carga.value = CargaUi.Exito(
                "Una por una", perfil, pedidos.size, System.currentTimeMillis() - inicio
            )
        }
    }

    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            val (perfil, pedidos) = coroutineScope {
                val a = async { repo.cargarPerfil() }      // las dos arrancan juntas
                val b = async { repo.cargarPedidos(false) }
                Pair(a.await(), b.await())                 // max(2 s, 3 s) = 3 s
            }
            _carga.value = CargaUi.Exito(
                "A la vez", perfil, pedidos.size, System.currentTimeMillis() - inicio
            )
        }
    }
    // TODO Commit 8 (Paso 7): simularError y manejo de errores
}
