package com.flores.mycar.data.prefs

import android.content.Context
import com.flores.mycar.domain.model.ModoTema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda el modo de tema (claro, oscuro o del sistema) que eligio la persona. */
interface TemaRepository {
    val modo: StateFlow<ModoTema>
    fun cambiar(modo: ModoTema)
}

/** Implementacion con SharedPreferences: el modo se conserva al cerrar la app. */
class TemaRepositoryPrefs(context: Context) : TemaRepository {

    private val prefs = context.applicationContext.getSharedPreferences("mycar_prefs", Context.MODE_PRIVATE)

    private val _modo = MutableStateFlow(leer())
    override val modo: StateFlow<ModoTema> = _modo.asStateFlow()

    override fun cambiar(modo: ModoTema) {
        prefs.edit().putString(CLAVE, modo.name).apply()
        _modo.value = modo
    }

    private fun leer(): ModoTema =
        runCatching { ModoTema.valueOf(prefs.getString(CLAVE, null) ?: ModoTema.SISTEMA.name) }
            .getOrDefault(ModoTema.SISTEMA)

    private companion object {
        const val CLAVE = "modo_tema"
    }
}
