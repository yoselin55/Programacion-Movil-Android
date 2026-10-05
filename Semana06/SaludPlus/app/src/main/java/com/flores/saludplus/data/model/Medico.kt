package com.flores.saludplus.data.model

// Relaciones:
// - Lo usa Repositorio (colección medicos)
// Medico asociado a una especialidad
data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val cmp: String,
    val calificacion: Double,
    val resenas: Int,
    val disponibilidad: String
)
