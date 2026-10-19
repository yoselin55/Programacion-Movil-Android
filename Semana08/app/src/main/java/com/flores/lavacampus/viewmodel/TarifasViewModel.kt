package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Servicio
import com.flores.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Tarifas vigentes: solo servicios activos (si el administrador desactiva uno, desaparece)
class TarifasViewModel(repo: LavaRepository) : ViewModel() {
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .map { lista -> lista.sortedBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
