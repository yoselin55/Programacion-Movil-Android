package com.flores.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.centrotareas.data.TareasRepository
import com.flores.centrotareas.model.CargaUi
import com.flores.centrotareas.model.PrimosUi
import com.flores.centrotareas.model.Tiempos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    // Commit 10 (Mejora 1): el ViewModel ya no calcula el avance, solo recoge el Flow del Repository
    fun tareaLarga() {
        if (_corriendo.value) return          // Commit 5: ya hay una tarea corriendo
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            try {
                _mensaje.value = "Trabajando..."
                _progreso.value = 0f
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repo.tareaConProgreso().collect { _progreso.value = it }
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

    // Commit 11 (Mejora 2): avance del cálculo de 0f a 1f
    private val _primosProgreso = MutableStateFlow(0f)
    val primosProgreso: StateFlow<Float> = _primosProgreso.asStateFlow()

    fun contar(hasta: Int) {
        if (_primos.value is PrimosUi.Calculando) return
        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            _primosProgreso.value = 0f          // Commit 11 (Mejora 2)
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta) { _primosProgreso.value = it }   // Commit 11 (Mejora 2)
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

    // Commit 8 (Paso 7): interruptor para simular un fallo en los pedidos
    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    // Commit 12 (Mejora 3): cada carga exitosa suma su tiempo a la tabla
    private val _tiempos = MutableStateFlow(Tiempos())
    val tiempos: StateFlow<Tiempos> = _tiempos.asStateFlow()

    private fun registrarTiempo(modo: String, ms: Long) {
        _tiempos.update {
            if (modo == "Una por una") it.copy(unaPorUna = it.unaPorUna + ms)
            else it.copy(aLaVez = it.aLaVez + ms)
        }
    }

    // Una sola función con try/catch que usan las dos cargas
    private fun cargar(modo: String, tarea: suspend () -> Pair<String, List<String>>) {
        if (_carga.value is CargaUi.Cargando) return
        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = tarea()
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito(modo, perfil, pedidos.size, ms)
                registrarTiempo(modo, ms)         // Commit 12 (Mejora 3)
            } catch (e: CancellationException) {
                throw e                                    // primero dejamos pasar la cancelación
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }

    // Commit 7 (Paso 6): una por una (secuencial)
    fun cargarUnaPorUna() = cargar("Una por una") {
        val perfil = repo.cargarPerfil()                   // 2 s
        val pedidos = repo.cargarPedidos(_simularError.value)  // + 3 s = 5 s
        Pair(perfil, pedidos)
    }

    // Commit 7 (Paso 6): a la vez (paralelo con async)
    fun cargarALaVez() = cargar("A la vez") {
        coroutineScope {
            val a = async { repo.cargarPerfil() }          // las dos arrancan juntas
            val b = async { repo.cargarPedidos(_simularError.value) }
            Pair(a.await(), b.await())                     // max(2 s, 3 s) = 3 s
        }
    }
}
