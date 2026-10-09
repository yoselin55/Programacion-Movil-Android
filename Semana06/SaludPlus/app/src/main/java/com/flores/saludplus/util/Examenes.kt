package com.flores.saludplus.util

import com.flores.saludplus.data.model.Resultado
import java.time.LocalDate
import java.time.format.DateTimeParseException

// Relaciones:
// - Crea objetos Resultado (data/model/Resultado.kt)
// - La usa ResultadosScreen (ui/screens/resultados) para armar los exámenes de cada cita
// - La prueba ResultadosTest (app/src/test)
// - Es Kotlin puro (java.time.LocalDate) para poder probarla con JUnit

// Fase 2: estados posibles de un resultado
const val ESTADO_DISPONIBLE = "Disponible"
const val ESTADO_EN_PROCESO = "En proceso"
const val ESTADO_PENDIENTE = "Pendiente"

// Fase 2: lista FIJA de exámenes por especialidad (clave = id de la especialidad en Repositorio)
val examenesPorEspecialidad: Map<Int, List<String>> = mapOf(
    1 to listOf("Hemograma completo", "Examen de orina"),               // Medicina General
    2 to listOf("Control de crecimiento", "Tamizaje de hemoglobina"),   // Pediatría
    3 to listOf("Papanicolaou", "Ecografía pélvica"),                   // Ginecología
    4 to listOf("Electrocardiograma", "Perfil lipídico"),               // Cardiología
    5 to listOf("Prueba de alergias", "Biopsia de piel"),               // Dermatología
    6 to listOf("Radiografía de columna", "Resonancia de rodilla"),     // Traumatología
    7 to listOf("Agudeza visual", "Fondo de ojo")                       // Oftalmología
)

// Fase 2: devuelve los exámenes de la especialidad con su estado según la fecha de la cita:
// "Disponible" si la cita fue antes de hoy, "En proceso" si es hoy y "Pendiente" si es después.
// Devuelve una lista vacía si la especialidad no existe; una fecha inválida se toma como "Pendiente".
fun resultadosDeCita(especialidadId: Int, fechaIso: String, hoy: LocalDate): List<Resultado> {
    val examenes = examenesPorEspecialidad[especialidadId] ?: return emptyList()
    val fecha = try {
        LocalDate.parse(fechaIso)
    } catch (e: DateTimeParseException) {
        null
    }
    val estado = when {
        fecha == null -> ESTADO_PENDIENTE
        fecha.isBefore(hoy) -> ESTADO_DISPONIBLE
        fecha.isEqual(hoy) -> ESTADO_EN_PROCESO
        else -> ESTADO_PENDIENTE
    }
    // El id combina especialidad y posición del examen (31, 32...) para que sea único por cita
    return examenes.mapIndexed { indice, titulo ->
        Resultado(especialidadId * 10 + indice + 1, titulo, especialidadId, fechaIso, estado)
    }
}
