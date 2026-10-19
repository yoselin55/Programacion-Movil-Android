package com.flores.lavacampus.model

// Resultado de la consulta de clientes (JOIN + COUNT + SUM), no es una tabla
data class ClienteResumen(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val cantidadPedidos: Int,
    val totalGastado: Double
)
