package com.flores.lavacampus.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.lavacampus.data.local.entity.Usuario
import com.flores.lavacampus.data.prefs.FotoPerfil
import com.flores.lavacampus.data.prefs.FotoPerfilStore
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.model.ValidacionRegistro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Maneja el inicio de sesion y recuerda el perfil activo (DataStore)
class SesionViewModel(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore,
    private val fotos: FotoPerfilStore
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

    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val recibirPedidos: StateFlow<Boolean> = prefs.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun cambiarModoOscuro(b: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(b) }
    }

    fun cambiarNotificaciones(b: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(b) }
    }

    fun cambiarRecibirPedidos(b: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirPedidos(b) }
    }

    // Error del cuadro "Cambiar de perfil" (se muestra dentro del dialogo)
    private val _errorCambio = MutableStateFlow<String?>(null)
    val errorCambio: StateFlow<String?> = _errorCambio.asStateFlow()

    fun limpiarErrorCambio() {
        _errorCambio.value = null
    }

    // Cambiar de perfil NO es un salto libre: hay que iniciar sesion con el otro perfil
    // (correo y clave del Administrador si eres Estudiante, y al reves)
    fun cambiarPerfil(correo: String, clave: String, onListo: (Rol) -> Unit) {
        val actual = _usuarioActual.value ?: return
        val destino = if (actual.rol == Rol.ADMIN.name) Rol.CLIENTE else Rol.ADMIN
        if (correo.isBlank() || clave.isBlank()) {
            _errorCambio.value = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo.trim(), clave)
            when {
                usuario == null -> _errorCambio.value = "Correo o contraseña incorrectos"
                usuario.rol != destino.name -> _errorCambio.value = "Ese usuario no es ${destino.texto}"
                else -> {
                    _errorCambio.value = null
                    prefs.guardarSesion(usuario.rol, usuario.id)
                    _usuarioActual.value = usuario
                    onListo(destino)
                }
            }
        }
    }

    // ---------- Foto de perfil ----------
    private val _versionFoto = MutableStateFlow(0)

    // Foto del usuario actual (null si no tiene: se muestran sus iniciales)
    val foto: StateFlow<FotoPerfil?> = combine(_usuarioActual, _versionFoto) { u, _ -> u?.let { fotos.foto(it.id) } }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _mensajeFoto = MutableStateFlow<String?>(null)
    val mensajeFoto: StateFlow<String?> = _mensajeFoto.asStateFlow()

    fun cambiarFoto(uri: Uri) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            if (fotos.guardar(usuario.id, uri)) {
                _mensajeFoto.value = null
                _versionFoto.value++
            } else {
                _mensajeFoto.value = "No se pudo usar esa imagen. Prueba con otra."
            }
        }
    }

    fun quitarFoto() {
        val usuario = _usuarioActual.value ?: return
        fotos.quitar(usuario.id)
        _versionFoto.value++
    }

    fun limpiarMensajeFoto() {
        _mensajeFoto.value = null
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            onListo()
        }
    }

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
                    _bienvenida.value = "¡Te damos la bienvenida, ${usuario.nombre.substringBefore(" ")}!"
                    onExito(rolSeleccionado)
                }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    // ---------- Bienvenida (se muestra una vez al entrar) ----------
    private val _bienvenida = MutableStateFlow<String?>(null)
    val bienvenida: StateFlow<String?> = _bienvenida.asStateFlow()

    fun limpiarBienvenida() {
        _bienvenida.value = null
    }

    // ---------- Registro de estudiantes (el administrador no se registra) ----------
    // campo -> mensaje de error; los campos validos no aparecen
    private val _erroresRegistro = MutableStateFlow<Map<String, String>>(emptyMap())
    val erroresRegistro: StateFlow<Map<String, String>> = _erroresRegistro.asStateFlow()

    fun limpiarErroresRegistro() {
        _erroresRegistro.value = emptyMap()
    }

    // Al empezar a corregir un campo, su mensaje desaparece
    fun limpiarErrorCampo(campo: String) {
        if (campo in _erroresRegistro.value) _erroresRegistro.value = _erroresRegistro.value - campo
    }

    fun registrar(
        nombre: String, codigo: String, correo: String, clave: String, confirmacion: String,
        onExito: (Rol) -> Unit
    ) {
        // Los espacios de mas se eliminan antes de validar ("carla   mendoza " -> "carla mendoza")
        val nombre = nombre.trim().replace(Regex("""\s+"""), " ")
        // 1) Validaciones de formato (no tocan la base)
        val errores = listOfNotNull(
            ValidacionRegistro.nombre(nombre)?.let { "nombre" to it },
            ValidacionRegistro.codigo(codigo)?.let { "codigo" to it },
            ValidacionRegistro.correo(correo)?.let { "correo" to it },
            ValidacionRegistro.clave(clave)?.let { "clave" to it },
            ValidacionRegistro.confirmacion(clave, confirmacion)?.let { "confirmacion" to it }
        ).toMap()
        if (errores.isNotEmpty()) {
            _erroresRegistro.value = errores
            return
        }
        viewModelScope.launch {
            // 2) Validaciones contra la base: correo y codigo unicos
            val correoLimpio = correo.trim().lowercase()
            val codigoLimpio = codigo.trim().uppercase()
            val repetidos = buildMap {
                if (repo.existeCorreo(correoLimpio)) put("correo", "Ese correo ya está registrado")
                if (repo.existeCodigo(codigoLimpio)) put("codigo", "Ese código ya está registrado")
            }
            if (repetidos.isNotEmpty()) {
                _erroresRegistro.value = repetidos
                return@launch
            }
            // 3) Se crea el estudiante y se inicia sesion con el
            val nuevo = repo.registrarUsuario(
                Usuario(
                    nombre = nombre.trim().split(" ").filter { it.isNotEmpty() }
                        .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } },
                    codigo = codigoLimpio,
                    correo = correoLimpio,
                    clave = clave,
                    rol = Rol.CLIENTE.name
                )
            )
            _erroresRegistro.value = emptyMap()
            prefs.guardarSesion(nuevo.rol, nuevo.id)
            _usuarioActual.value = nuevo
            _bienvenida.value = "¡Te damos la bienvenida, ${nuevo.nombre.substringBefore(" ")}!"
            onExito(Rol.CLIENTE)
        }
    }
}
