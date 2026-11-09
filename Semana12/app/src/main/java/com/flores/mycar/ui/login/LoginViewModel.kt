package com.flores.mycar.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.auth.AuthRepository
import com.flores.mycar.data.auth.CredencialesInvalidasException
import com.flores.mycar.domain.model.Sesion
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de login. Cuando "sesion" no es null, el ingreso fue exitoso. */
data class LoginEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: Sesion? = null
)

class LoginViewModel(
    private val auth: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun iniciarSesion(correo: String, clave: String) {
        // Campos vacios: se avisa y NO se llama a Firebase.
        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = LoginEstado(error = "Escribe tu correo y tu contraseña")
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado(cargando = true)
            auth.iniciarSesion(correo.trim(), clave)
                .onSuccess { _estado.value = LoginEstado(sesion = it) }
                .onFailure { _estado.value = LoginEstado(error = mensajeDe(it)) }
        }
    }

    /** La pantalla ya uso la sesion: se limpia para que un nuevo login no entre solo. */
    fun sesionConsumida() {
        _estado.value = LoginEstado()
    }

    private fun mensajeDe(e: Throwable) = when (e) {
        is CredencialesInvalidasException -> "Correo o contraseña incorrectos"
        is IOException -> "No hay conexión a internet"
        else -> "No se pudo iniciar sesión. Inténtalo de nuevo"
    }
}
