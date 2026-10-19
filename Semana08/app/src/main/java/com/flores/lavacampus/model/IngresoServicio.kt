package com.flores.lavacampus.model

// Resultado de SUM(subtotal) GROUP BY servicio, para el grafico de barras
data class IngresoServicio(
    val nombre: String,
    val total: Double
)
