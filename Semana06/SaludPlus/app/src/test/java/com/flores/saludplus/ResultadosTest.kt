package com.flores.saludplus

import com.flores.saludplus.util.resultadosDeCita
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// Relaciones:
// - Prueba resultadosDeCita (util/Examenes.kt)
// - Usa JUnit 4 y java.time.LocalDate

// Fase 2: pruebas de los resultados que dependen de la fecha de la cita
class ResultadosTest {

    private val hoy = LocalDate.of(2026, 10, 8)
    private val ginecologia = 3

    // Verifica que una cita de Ginecología pasada da sus 2 exámenes como "Disponible"
    @Test
    fun ginecologia_fechaPasada_disponible() {
        val resultados = resultadosDeCita(ginecologia, "2026-10-01", hoy)
        assertEquals(listOf("Papanicolaou", "Ecografía pélvica"), resultados.map { it.titulo })
        assertTrue(resultados.all { it.estado == "Disponible" && it.especialidadId == ginecologia })
    }

    // Verifica que una cita de Ginecología de hoy da sus exámenes "En proceso"
    @Test
    fun ginecologia_fechaDeHoy_enProceso() {
        val resultados = resultadosDeCita(ginecologia, "2026-10-08", hoy)
        assertEquals(2, resultados.size)
        assertTrue(resultados.all { it.estado == "En proceso" })
    }

    // Verifica que una cita de Ginecología futura da sus exámenes "Pendiente"
    @Test
    fun ginecologia_fechaFutura_pendiente() {
        val resultados = resultadosDeCita(ginecologia, "2026-10-15", hoy)
        assertEquals(2, resultados.size)
        assertTrue(resultados.all { it.estado == "Pendiente" })
    }

    // Verifica que una especialidad inexistente devuelve una lista vacía
    @Test
    fun especialidadInexistente_listaVacia() {
        assertTrue(resultadosDeCita(99, "2026-10-01", hoy).isEmpty())
    }
}
