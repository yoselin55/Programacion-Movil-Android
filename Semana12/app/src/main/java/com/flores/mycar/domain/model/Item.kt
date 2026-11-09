package com.flores.mycar.domain.model

/**
 * Modelo que usa la UI. En Mycar un Item es una marca de autos (NHTSA).
 * El id es Int porque Make_ID es numerico.
 */
data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false
)
