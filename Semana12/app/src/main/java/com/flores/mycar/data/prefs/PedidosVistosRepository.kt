package com.flores.mycar.data.prefs

import android.content.Context

/** Recuerda que cambios de pedidos ya vio cada persona, para contar los que le faltan por ver. */
interface PedidosVistosRepository {
    fun vistos(uid: String): Set<String>
    fun guardar(uid: String, claves: Set<String>)
}

class PedidosVistosPrefs(context: Context) : PedidosVistosRepository {

    private val prefs = context.applicationContext.getSharedPreferences("mycar_pedidos_vistos", Context.MODE_PRIVATE)

    override fun vistos(uid: String): Set<String> = prefs.getStringSet("vistos_$uid", emptySet()).orEmpty().toSet()

    override fun guardar(uid: String, claves: Set<String>) {
        prefs.edit().putStringSet("vistos_$uid", claves).apply()
    }
}
