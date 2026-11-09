package com.flores.mycar.domain.model

/**
 * Modelo que usa la UI. En Mycar un Item es una marca de autos (NHTSA).
 * El id es Int porque MakeId es numerico. Los modelos solo se llenan en el detalle.
 */
data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val modelos: List<String> = emptyList()
)
