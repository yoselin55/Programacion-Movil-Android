package com.flores.mycar.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.fichas.LogoMarcaRepository
import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Item
import com.flores.mycar.ui.CargadorLogos
import com.flores.mycar.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel de Favoritos: observa los favoritos guardados en Room. */
class FavoritosViewModel(
    private val repo: ItemRepository,
    logos: LogoMarcaRepository? = null
) : ViewModel() {

    private val cargadorLogos = CargadorLogos(logos, viewModelScope)
    val logos: StateFlow<Map<String, String>> = cargadorLogos.urls

    fun pedirLogo(marca: String) = cargadorLogos.pedir(marca)

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            repo.favoritos.collect { _estado.value = UiState.Exito(it) }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }
}
