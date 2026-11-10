package com.flores.mycar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flores.mycar.data.perfil.FotoPerfil
import com.flores.mycar.data.perfil.FotoPerfilRepository
import com.flores.mycar.data.prefs.ComprasRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Datos del menu lateral: cuantos autos hay en el carrito y la foto de perfil. */
@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModel(
    comprasRepo: ComprasRepository,
    fotoRepo: FotoPerfilRepository
) : ViewModel() {

    private val uid = MutableStateFlow<String?>(null)

    val carrito: StateFlow<Int> = uid
        .flatMapLatest { u -> if (u == null) flowOf(0) else comprasRepo.estado(u).map { it.carrito.size } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val foto: StateFlow<FotoPerfil?> = uid
        .flatMapLatest { u -> if (u == null) flowOf(null) else fotoRepo.foto(u) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun usuario(uid: String) {
        this.uid.value = uid
    }
}
