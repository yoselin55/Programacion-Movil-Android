package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Notificacion
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Notificaciones del usuario que inicio sesion (estudiante o administrador).
// Son Flow de Room: cuando el otro perfil cambia un pedido, la lista y el contador se actualizan solos
class NotificacionesViewModel(
    private val repo: LavaRepository,
    private val usuario: Usuario
) : ViewModel() {

    // null = todavia cargando
    val notificaciones: StateFlow<List<Notificacion>?> = repo.observarNotificaciones(usuario.id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Numero que se ve sobre la campana
    val noLeidas: StateFlow<Int> = repo.observarNoLeidas(usuario.id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun marcarLeida(id: Int) {
        viewModelScope.launch { repo.marcarNotificacionLeida(id) }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch { repo.marcarNotificacionesLeidas(usuario.id) }
    }

    fun borrarTodas() {
        viewModelScope.launch { repo.borrarNotificaciones(usuario.id) }
    }
}
