package com.flores.mycar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tabla "items" de Room: una marca de autos guardada en el celular. */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false
)
