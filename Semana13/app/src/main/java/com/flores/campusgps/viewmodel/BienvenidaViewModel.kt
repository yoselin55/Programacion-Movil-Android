package com.flores.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.campusgps.data.PreferenciasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Mejora 3 (bienvenida): guarda y expone el nombre del usuario.
class BienvenidaViewModel(
    private val preferenciasRepo: PreferenciasRepository
) : ViewModel() {

    // null mientras se lee el archivo; después, el nombre guardado (o texto vacío si no hay).
    val nombre: StateFlow<String?> = preferenciasRepo.nombre
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun guardarNombre(nombre: String) {
        viewModelScope.launch { preferenciasRepo.guardarNombre(nombre.trim()) }
    }
}
