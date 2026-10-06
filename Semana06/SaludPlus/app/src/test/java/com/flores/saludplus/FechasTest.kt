package com.flores.saludplus

import com.flores.saludplus.util.Fechas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

// Relaciones:
// - Prueba el object Fechas (util/Fechas.kt)
// - Usa JUnit 4 y java.time.LocalDate

// Fase 2: pruebas unitarias de la utilidad de fechas
class FechasTest {

    private val martes = LocalDate.of(2026, 10, 6)

    // Verifica que desde un martes se obtienen mar-vie y el lunes siguiente (6, 7, 8, 9 y 12)
    @Test
    fun diasHabiles_desdeMartes_saltaFinDeSemana() {
        val dias = Fechas.diasHabiles(martes).map { it.dayOfMonth }
        assertEquals(listOf(6, 7, 8, 9, 12), dias)
    }

    // Verifica que desde un sábado la lista empieza el lunes 2026-10-12
    @Test
    fun diasHabiles_desdeSabado_empiezaLunes() {
        val dias = Fechas.diasHabiles(LocalDate.of(2026, 10, 10))
        assertEquals(LocalDate.of(2026, 10, 12), dias.first())
    }

    // Verifica que ninguna semana (desplazamientos 0 a 8) contiene sábados ni domingos
    @Test
    fun semana_nuncaContieneFinesDeSemana() {
        for (n in 0..8) {
            Fechas.semana(martes, n).forEach { fecha ->
                assertTrue("Fecha no hábil en semana $n: $fecha", Fechas.esHabil(fecha))
            }
        }
    }

    // Verifica que la semana 1 empieza exactamente 7 días después que la semana 0
    @Test
    fun semana_desplazamientoUno_sumaSieteDias() {
        val inicio0 = Fechas.semana(martes, 0).first()
        val inicio1 = Fechas.semana(martes, 1).first()
        assertEquals(inicio0.plusDays(7), inicio1)
    }

    // Verifica el encabezado de mes: semana 0 en octubre y semana 4 en noviembre
    @Test
    fun mesYAnio_deLaPrimeraFechaDeSemana() {
        assertEquals("Octubre 2026", Fechas.mesYAnio(Fechas.semana(martes, 0).first()))
        assertEquals("Noviembre 2026", Fechas.mesYAnio(Fechas.semana(martes, 4).first()))
    }

    // Verifica los textos en español para el miércoles 2026-09-16 (con "setiembre")
    @Test
    fun textos_miercoles16Setiembre() {
        val fecha = LocalDate.of(2026, 9, 16)
        assertEquals("Miércoles 16 de setiembre 2026", Fechas.textoLargo(fecha))
        assertEquals("Mié 16 set 2026", Fechas.textoCorto(fecha))
        assertEquals("Setiembre 2026", Fechas.mesYAnio(fecha))
    }
}
