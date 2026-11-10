package com.flores.mycar.notification

/** Contrato para mostrar notificaciones. El ViewModel no conoce Context: usa esta interface. */
interface Notificador {
    fun mostrar(titulo: String, texto: String)
}

/** Textos de la notificacion local que avisa que ya estan los ultimos autos (se muestra al sincronizar). */
object AvisoAutos {
    const val TITULO = "MyCar"
    const val TEXTO = "Ya están los últimos autos. ¡Entra y míralos!"
}
