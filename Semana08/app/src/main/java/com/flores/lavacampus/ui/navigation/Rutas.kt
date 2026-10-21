package com.flores.lavacampus.ui.navigation

// Nombres de los destinos de navegacion
object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INICIO = "inicio"   // estudiante
    const val PANEL = "panel"     // administrador
    const val NUEVO_PEDIDO = "nuevoPedido?repetir={repetir}" // repetir = id del pedido a repetir (0 = pedido vacio)
    const val DETALLE = "detalle/{id}"
    const val PEDIDOS = "pedidos"
    const val TARIFAS = "tarifas"
    const val PERFIL = "perfil"
    const val BANDEJA = "bandeja"
    const val SERVICIOS = "servicios"
    const val CLIENTES = "clientes"
    const val AJUSTES = "ajustes"
    const val GESTION = "gestion/{id}"
    const val SERVICIO = "servicio/{id}"

    fun nuevoPedido(repetir: Int = 0) = "nuevoPedido?repetir=$repetir"
    fun detalle(id: Int) = "detalle/$id"
    fun gestion(id: Int) = "gestion/$id"
    fun servicio(id: Int) = "servicio/$id"
}
