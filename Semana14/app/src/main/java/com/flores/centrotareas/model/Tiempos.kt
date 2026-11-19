package com.flores.centrotareas.model

// Commit 12 (Mejora 3): tiempos acumulados de cada modo de carga
data class Tiempos(
    val unaPorUna: List<Long> = emptyList(),
    val aLaVez: List<Long> = emptyList()
) {
    val hayDatos: Boolean get() = unaPorUna.isNotEmpty() || aLaVez.isNotEmpty()
    val promedioUnaPorUna: Long? get() = unaPorUna.takeIf { it.isNotEmpty() }?.average()?.toLong()
    val promedioALaVez: Long? get() = aLaVez.takeIf { it.isNotEmpty() }?.average()?.toLong()
}
