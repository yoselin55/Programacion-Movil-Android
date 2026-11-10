package com.flores.mycar.data.prefs

import android.content.Context

/** Recuerda el correo de la persona para no escribirlo cada vez ("Recordarme"). */
interface AccesoRepository {
    fun correoRecordado(): String?
    fun recordar(correo: String?)
}

class AccesoRepositoryPrefs(context: Context) : AccesoRepository {

    private val prefs = context.applicationContext.getSharedPreferences("mycar_prefs", Context.MODE_PRIVATE)

    override fun correoRecordado(): String? = prefs.getString(CLAVE, null)

    override fun recordar(correo: String?) {
        prefs.edit().apply { if (correo == null) remove(CLAVE) else putString(CLAVE, correo) }.apply()
    }

    private companion object {
        const val CLAVE = "correo_recordado"
    }
}
