package com.flores.mycar.notification

/** Contrato para mostrar notificaciones. El ViewModel no conoce Context: usa esta interface. */
interface Notificador {
    fun mostrar(titulo: String, texto: String)
}
