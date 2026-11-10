package com.flores.mycar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.auth.AuthRepository
import com.flores.mycar.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Guarda la sesion de la persona: al abrir la app revisa si ya habia una iniciada. */
class AppViewModel(
    private val auth: AuthRepository
) : ViewModel() {

    // Exito(null) = no hay sesion y hay que mostrar el login.
    private val _sesion = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val sesion: StateFlow<UiState<Sesion?>> = _sesion.asStateFlow()

    // Mensaje de bienvenida: solo aparece justo despues de iniciar sesion o registrarse.
    private val _bienvenida = MutableStateFlow<String?>(null)
    val bienvenida: StateFlow<String?> = _bienvenida.asStateFlow()

    init {
        viewModelScope.launch {
            _sesion.value = UiState.Exito(auth.sesionActual())
        }
    }

    fun sesionIniciada(sesion: Sesion) {
        val nombre = sesion.nombre.ifBlank { sesion.correo.substringBefore("@") }
        _bienvenida.value = "Bienvenido, $nombre"
        _sesion.value = UiState.Exito(sesion)
    }

    fun bienvenidaMostrada() {
        _bienvenida.value = null
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _bienvenida.value = null
        _sesion.value = UiState.Exito(null)
    }
}
