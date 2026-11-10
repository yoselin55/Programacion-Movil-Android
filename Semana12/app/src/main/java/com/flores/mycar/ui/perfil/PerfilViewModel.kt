package com.flores.mycar.ui.perfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.perfil.FotoPerfil
import com.flores.mycar.data.perfil.FotoPerfilRepository
import com.flores.mycar.data.perfil.PerfilRepository
import com.flores.mycar.domain.model.PerfilDatos
import com.flores.mycar.domain.model.Sesion
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilEstado(
    val datos: PerfilDatos = PerfilDatos(),
    val foto: FotoPerfil? = null,
    val guardando: Boolean = false,
    val mensaje: String? = null
)

/** Perfil de la persona: sus datos, su foto y la forma de editarlos. */
class PerfilViewModel(
    private val perfilRepo: PerfilRepository,
    private val fotoRepo: FotoPerfilRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PerfilEstado())
    val estado: StateFlow<PerfilEstado> = _estado.asStateFlow()

    private var uid: String? = null
    private var trabajo: Job? = null

    /** Carga los datos de esa persona. Si ya los esta observando, no repite. */
    fun usuario(sesion: Sesion) {
        if (uid == sesion.uid && trabajo?.isActive == true) return
        uid = sesion.uid
        trabajo?.cancel()
        trabajo = viewModelScope.launch {
            val datos = perfilRepo.leer(sesion.uid)
            _estado.update { it.copy(datos = datos.copy(nombre = datos.nombre.ifBlank { sesion.nombre })) }
            fotoRepo.foto(sesion.uid).collect { foto -> _estado.update { it.copy(foto = foto) } }
        }
    }

    fun guardarDatos(nombre: String, telefono: String, ciudad: String) {
        val id = uid ?: return
        if (nombre.isBlank()) {
            _estado.update { it.copy(mensaje = "Escribe tu nombre") }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, mensaje = null) }
            val nuevos = PerfilDatos(nombre.trim(), telefono.trim(), ciudad.trim())
            perfilRepo.guardar(id, nuevos)
                .onSuccess { _estado.update { it.copy(datos = nuevos, guardando = false, mensaje = "Datos guardados") } }
                .onFailure { _estado.update { it.copy(guardando = false, mensaje = "No se pudieron guardar los datos") } }
        }
    }

    fun cambiarFoto(origen: Uri) {
        val id = uid ?: return
        viewModelScope.launch {
            fotoRepo.guardar(id, origen)
                .onSuccess { _estado.update { it.copy(mensaje = "Foto actualizada") } }
                .onFailure { _estado.update { it.copy(mensaje = "No se pudo guardar la foto") } }
        }
    }

    fun mensajeMostrado() {
        _estado.update { it.copy(mensaje = null) }
    }
}
