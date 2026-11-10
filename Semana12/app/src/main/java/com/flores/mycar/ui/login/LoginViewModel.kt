package com.flores.mycar.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.auth.AuthRepository
import com.flores.mycar.data.auth.ClaveDebilException
import com.flores.mycar.data.auth.CorreoEnUsoException
import com.flores.mycar.data.auth.CorreoInvalidoException
import com.flores.mycar.data.auth.CredencialesInvalidasException
import com.flores.mycar.data.prefs.AccesoRepository
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
    val info: String? = null,
    val sesion: Sesion? = null
)

class LoginViewModel(
    private val auth: AuthRepository,
    private val acceso: AccesoRepository
) : ViewModel() {

    /** Correo guardado con "Recordarme", o null si la persona no lo pidio. */
    val correoRecordado: String? = acceso.correoRecordado()

    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado.asStateFlow()

    fun iniciarSesion(correo: String, clave: String, recordar: Boolean = false) {
        // Campos vacios: se avisa y NO se llama a Firebase.
        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = LoginEstado(error = "Escribe tu correo y tu contraseña")
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado(cargando = true)
            auth.iniciarSesion(limpiar(correo), clave)
                .onSuccess {
                    acceso.recordar(if (recordar) limpiar(correo) else null)
                    _estado.value = LoginEstado(sesion = it)
                }
                .onFailure { _estado.value = LoginEstado(error = mensajeDe(it)) }
        }
    }

    /** Crea una cuenta nueva (siempre con rol usuario) y entra directamente. */
    fun registrar(nombre: String, correo: String, clave: String, confirmar: String) {
        val error = when {
            nombre.isBlank() || correo.isBlank() || clave.isBlank() || confirmar.isBlank() ->
                "Completa todos los campos"
            !CORREO.matches(limpiar(correo)) -> "Escribe un correo válido"
            clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            clave != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _estado.value = LoginEstado(error = error)
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado(cargando = true)
            auth.registrar(nombre.trim(), limpiar(correo), clave)
                .onSuccess { _estado.value = LoginEstado(sesion = it) }
                .onFailure { _estado.value = LoginEstado(error = mensajeDe(it)) }
        }
    }

    /** "¿Olvidaste tu contraseña?": envia un correo para restablecerla. */
    fun recuperarClave(correo: String) {
        if (!CORREO.matches(limpiar(correo))) {
            _estado.value = LoginEstado(error = "Escribe tu correo para enviarte el enlace")
            return
        }
        viewModelScope.launch {
            _estado.value = LoginEstado(cargando = true)
            auth.recuperarClave(limpiar(correo))
                .onSuccess {
                    _estado.value = LoginEstado(info = "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña.")
                }
                .onFailure { _estado.value = LoginEstado(error = mensajeDe(it)) }
        }
    }

    /** La pantalla ya uso la sesion: se limpia para que un nuevo login no entre solo. */
    fun sesionConsumida() {
        _estado.value = LoginEstado()
    }

    /** Al cambiar entre Iniciar sesion y Registrarse se borra el aviso anterior. */
    fun limpiarError() {
        if (_estado.value.error != null || _estado.value.info != null) _estado.value = LoginEstado()
    }

    private fun mensajeDe(e: Throwable) = when (e) {
        is CredencialesInvalidasException -> "Correo o contraseña incorrectos"
        is CorreoEnUsoException -> "Ese correo ya tiene una cuenta. Inicia sesión."
        is ClaveDebilException -> "La contraseña es muy débil. Usa al menos 6 caracteres."
        is CorreoInvalidoException -> "Escribe un correo válido"
        is IOException -> "No hay conexión a internet"
        else -> "No se pudo completar la operación. Inténtalo de nuevo"
    }

    /**
     * Deja el correo limpio: sin espacios ni caracteres invisibles (los que a veces agregan el teclado
     * o el autocompletado y hacen que Firebase diga que el correo tiene un formato incorrecto) y en minusculas.
     */
    private fun limpiar(correo: String): String = correo
        .filter { !it.isWhitespace() && !it.isISOControl() && Character.getType(it) != Character.FORMAT.toInt() && it != '\uFEFF' }
        .lowercase()

    private companion object {
        val CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}
