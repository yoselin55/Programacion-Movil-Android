package com.flores.lavacampus.model

// Que tipo de aviso es: define el icono y el color en la lista de notificaciones
enum class TipoNotificacion {
    NUEVO,       // llega un pedido nuevo (administrador)
    RECIBIDO,
    EN_PROCESO,
    LISTO,
    ENTREGADO,
    CANCELADO,
    PAGO,
    ALERTA;      // pedido atrasado (administrador)

    companion object {
        fun de(valor: String): TipoNotificacion = entries.firstOrNull { it.name == valor } ?: ALERTA
    }
}

// Titulo y mensaje de cada notificacion, en un solo lugar
object TextosNotificacion {
    class Texto(val tipo: TipoNotificacion, val titulo: String, val mensaje: String)

    private fun codigo(id: Int) = "#" + id.toString().padStart(3, '0')

    // Para el estudiante, cuando el administrador cambia su pedido
    fun alEstudiante(estado: EstadoPedido, pedidoId: Int, casillero: String?): Texto? = when (estado) {
        EstadoPedido.EN_PROCESO -> Texto(
            TipoNotificacion.EN_PROCESO, "Tu pedido ${codigo(pedidoId)} está en proceso",
            "Ya estamos lavando tu ropa."
        )
        EstadoPedido.LISTO -> Texto(
            TipoNotificacion.LISTO, "Tu pedido ${codigo(pedidoId)} está listo",
            if (casillero != null) "Recógelo en el casillero $casillero." else "Ya puedes recogerlo."
        )
        EstadoPedido.ENTREGADO -> Texto(
            TipoNotificacion.ENTREGADO, "Pedido ${codigo(pedidoId)} entregado",
            "Gracias por usar LavaCampus."
        )
        EstadoPedido.CANCELADO -> Texto(
            TipoNotificacion.CANCELADO, "Pedido ${codigo(pedidoId)} cancelado",
            "La lavandería canceló tu pedido."
        )
        EstadoPedido.RECIBIDO -> null
    }

    fun pagoRegistrado(pedidoId: Int, total: String) = Texto(
        TipoNotificacion.PAGO, "Pago registrado",
        "Se registró el pago de $total del pedido ${codigo(pedidoId)}."
    )

    // Para el administrador
    fun pedidoNuevo(pedidoId: Int, estudiante: String, resumen: String) = Texto(
        TipoNotificacion.NUEVO, "Nuevo pedido ${codigo(pedidoId)}",
        "$estudiante pidió $resumen."
    )

    fun canceladoPorEstudiante(pedidoId: Int, estudiante: String) = Texto(
        TipoNotificacion.CANCELADO, "Pedido ${codigo(pedidoId)} cancelado",
        "$estudiante canceló su pedido."
    )
}
