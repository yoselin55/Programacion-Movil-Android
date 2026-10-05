package com.flores.saludplus.data.model

import androidx.compose.ui.graphics.vector.ImageVector

// Relaciones:
// - Lo usa Repositorio (colección especialidades)
// Especialidad medica con su icono para las listas
data class Especialidad(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val icono: ImageVector
)
