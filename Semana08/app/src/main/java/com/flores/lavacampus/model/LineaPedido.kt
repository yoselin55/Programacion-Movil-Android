package com.flores.lavacampus.model

// Una linea elegida en "Nuevo pedido": que servicio y cuanta cantidad
data class LineaPedido(
    val servicioId: Int,
    val cantidad: Double
)
