package com.flores.mycar.ui.perfil

import androidx.lifecycle.ViewModel
import com.flores.mycar.data.prefs.TemaRepository
import com.flores.mycar.domain.model.ModoTema
import kotlinx.coroutines.flow.StateFlow

/** Expone el modo de tema actual y permite cambiarlo desde Perfil. */
class TemaViewModel(
    private val repo: TemaRepository
) : ViewModel() {

    val modo: StateFlow<ModoTema> = repo.modo

    fun cambiar(modo: ModoTema) = repo.cambiar(modo)
}
