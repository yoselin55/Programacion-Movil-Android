package com.flores.saludplus.data.model

// Relaciones:
// - Lo crea resultadosDeCita (util/Examenes.kt) a partir de una cita del paciente
// - Lo usa ResultadosScreen (ui/screens/resultados) para dibujar cada examen

// Fase 2: resultado de un examen del paciente: especialidad de la cita que lo originó,
// fecha en formato ISO (2026-10-05) y estado "Disponible", "En proceso" o "Pendiente"
data class Resultado(
    val id: Int,
    val titulo: String,
    val especialidadId: Int,
    val fecha: String,
    val estado: String
)
