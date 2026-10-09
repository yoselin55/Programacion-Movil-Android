package com.flores.saludplus.data.model

import java.time.DayOfWeek

// Relaciones:
// - Lo usa Repositorio (colección medicos)
// Fase 3: médico con su sede, código, teléfono y los horarios en los que atiende
// (horario: para cada día de la semana en que atiende, sus horas de inicio "09:30")
data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val sedeId: Int,
    val codigo: String,
    val telefono: String,
    val calificacion: Double,
    val resenas: Int,
    val horario: Map<DayOfWeek, List<String>>
) {
    // Fase 3: indica si el médico atiende ese día de la semana
    fun atiende(dia: DayOfWeek): Boolean = horario[dia].orEmpty().isNotEmpty()

    // Fase 3: horas de atención de un día de la semana (vacía si no atiende)
    fun horasDe(dia: DayOfWeek): List<String> = horario[dia].orEmpty()

    // Fase 3: la mayor cantidad de horas que atiende en un día (para dimensionar la cuadrícula)
    val horasMaximasPorDia: Int
        get() = horario.values.maxOfOrNull { it.size } ?: 0
}
