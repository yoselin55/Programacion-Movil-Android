package com.flores.mycar.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.autos.MarcasEnVenta
import com.flores.mycar.data.fichas.LogoMarcaRepository
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Item
import com.flores.mycar.notification.AvisoAutos
import com.flores.mycar.notification.Notificador
import com.flores.mycar.ui.CargadorLogos
import com.flores.mycar.ui.UiState
import com.flores.mycar.ui.mensajeDe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel de la lista: depende de la interface ItemRepository, nunca de la clase concreta. */
class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador,
    logos: LogoMarcaRepository? = null,
    desactivadas: Flow<Set<String>> = flowOf(emptySet())
) : ViewModel() {

    private val apagadas = desactivadas

    private val cargadorLogos = CargadorLogos(logos, viewModelScope)

    /** Logo de cada marca (por nombre), a medida que se van encontrando. */
    val logos: StateFlow<Map<String, String>> = cargadorLogos.urls

    fun pedirLogo(marca: String) = cargadorLogos.pedir(marca)

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private var refrescoTerminado = false

    init {
        viewModelScope.launch {
            combine(repo.items, apagadas.catch { emit(emptySet()) }) { lista, off ->
                lista.filter { "marca_${it.id}" !in off }
            }.collect { lista ->
                // Mientras no termine el primer refresco, una lista vacia no se muestra (sigue Cargando).
                if (lista.isNotEmpty() || refrescoTerminado) _estado.value = UiState.Exito(
                        lista.filter { MarcasEnVenta.enVenta(it.titulo) }.sortedBy { MarcasEnVenta.orden(it.titulo) }
                    )
            }
        }
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            if (_estado.value is UiState.Error) _estado.value = UiState.Cargando
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar(AvisoAutos.TITULO, AvisoAutos.TEXTO)
                }
                .onFailure { e ->
                    if (repo.cantidadGuardada() == 0) _estado.value = UiState.Error(mensajeDe(e))
                    else _aviso.value = "Sin conexión: mostrando datos guardados"
                }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}
