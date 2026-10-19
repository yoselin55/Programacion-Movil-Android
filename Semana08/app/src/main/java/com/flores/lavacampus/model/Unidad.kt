package com.flores.lavacampus.model

// Unidad de cobro de un servicio y sus reglas de cantidad
enum class Unidad(val paso: Double, val minimo: Double, val maximo: Double, val texto: String, val plural: String) {
    KG(paso = 0.5, minimo = 1.0, maximo = 10.0, texto = "kg", plural = "kg"),
    PRENDA(paso = 1.0, minimo = 1.0, maximo = 30.0, texto = "prenda", plural = "prendas"),
    UNIDAD(paso = 1.0, minimo = 1.0, maximo = 30.0, texto = "unidad", plural = "unidades");

    companion object {
        fun de(valor: String): Unidad = entries.firstOrNull { it.name == valor } ?: UNIDAD
    }
}
