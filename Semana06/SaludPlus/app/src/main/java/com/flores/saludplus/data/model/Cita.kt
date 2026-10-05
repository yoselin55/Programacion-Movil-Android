package com.flores.saludplus.data.model

// Relaciones:
// - Lo usa Repositorio (colección citas)

// Cita agendada; fecha en formato ISO (2026-09-16) y hora "09:30"
data class Cita(
    val id: Int,
    val usuarioId: Int,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val motivo: String
)
