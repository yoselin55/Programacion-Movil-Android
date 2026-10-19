package com.flores.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Maneja el inicio de sesion y recuerda el perfil activo (DataStore)
class SesionViewModel(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // true mientras se revisa si ya hay un perfil guardado (el Splash espera)
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        // Si ya hay perfil guardado, se recupera el usuario y se salta el login
        viewModelScope.launch {
            val rol = prefs.perfilActivo.first()
            val id = prefs.usuarioId.first()
            if (rol.isNotEmpty() && id > 0) _usuarioActual.value = repo.obtenerUsuario(id)
            _cargando.value = false
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol, onExito: (Rol) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo.trim(), clave)
            when {
                usuario == null -> _error.value = "Correo o contraseña incorrectos"
                usuario.rol != rolSeleccionado.name ->
                    _error.value = "Este usuario no pertenece al perfil ${rolSeleccionado.texto}"
                else -> {
                    _error.value = null
                    prefs.guardarSesion(usuario.rol, usuario.id)
                    _usuarioActual.value = usuario
                    onExito(rolSeleccionado)
                }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
