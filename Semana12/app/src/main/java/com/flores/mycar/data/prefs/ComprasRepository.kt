package com.flores.mycar.data.prefs

import android.content.Context
import com.flores.mycar.domain.model.ComprasEstado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Carrito de compras y autos favoritos de cada persona. Se guardan en el celular. */
interface ComprasRepository {
    fun estado(uid: String): Flow<ComprasEstado>
    fun alternarCarrito(uid: String, autoId: String)
    fun quitarDelCarrito(uid: String, autoId: String)
    fun alternarFavorito(uid: String, autoId: String)
}

class ComprasRepositoryPrefs(context: Context) : ComprasRepository {

    private val prefs = context.applicationContext.getSharedPreferences("mycar_compras", Context.MODE_PRIVATE)

    // uid -> carrito y favoritos
    private val estados = MutableStateFlow(cargarTodo())

    override fun estado(uid: String): Flow<ComprasEstado> =
        estados.map { it[uid] ?: ComprasEstado() }.distinctUntilChanged()

    override fun alternarCarrito(uid: String, autoId: String) = cambiar(uid) { e ->
        e.copy(carrito = if (autoId in e.carrito) e.carrito - autoId else e.carrito + autoId)
    }

    override fun quitarDelCarrito(uid: String, autoId: String) = cambiar(uid) { e ->
        e.copy(carrito = e.carrito - autoId)
    }

    override fun alternarFavorito(uid: String, autoId: String) = cambiar(uid) { e ->
        e.copy(favoritos = if (autoId in e.favoritos) e.favoritos - autoId else e.favoritos + autoId)
    }

    private fun cambiar(uid: String, f: (ComprasEstado) -> ComprasEstado) {
        estados.update { mapa ->
            val nuevo = f(mapa[uid] ?: ComprasEstado())
            prefs.edit()
                .putStringSet("carrito_$uid", nuevo.carrito)
                .putStringSet("fav_$uid", nuevo.favoritos)
                .apply()
            mapa + (uid to nuevo)
        }
    }

    private fun cargarTodo(): Map<String, ComprasEstado> {
        val uids = prefs.all.keys.map { it.substringAfter('_') }.toSet()
        return uids.associateWith { uid ->
            ComprasEstado(
                carrito = prefs.getStringSet("carrito_$uid", emptySet()).orEmpty().toSet(),
                favoritos = prefs.getStringSet("fav_$uid", emptySet()).orEmpty().toSet()
            )
        }
    }
}
