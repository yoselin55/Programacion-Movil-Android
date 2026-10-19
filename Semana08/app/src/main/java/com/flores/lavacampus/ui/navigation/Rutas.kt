package com.flores.lavacampus.ui.navigation

// Nombres de los destinos de navegacion
object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INICIO = "inicio"   // estudiante
    const val PANEL = "panel"     // administrador
    const val NUEVO_PEDIDO = "nuevoPedido"
    const val DETALLE = "detalle/{id}"
    const val PEDIDOS = "pedidos"
    const val TARIFAS = "tarifas"
    const val PERFIL = "perfil"

    fun detalle(id: Int) = "detalle/$id"
}
