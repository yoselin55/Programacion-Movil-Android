package com.flores.campusgps.model

// Commit 1: modelos de datos de la app (posición, lugar y estados de la pantalla).

// Commit 1: coordenadas del usuario.
data class Posicion(val latitud: Double, val longitud: Double)

// Commit 1: un lugar del campus.
data class Lugar(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double
)

// metros = null mientras todavía no se conoce la ubicación del usuario
data class LugarConDistancia(val lugar: Lugar, val metros: Float?)

// Commit 1: estados que dibuja la pantalla (inicial, buscando, lista o error).
sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(val mensaje: String) : UbicacionUi
}
