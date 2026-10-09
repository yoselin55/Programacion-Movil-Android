package com.flores.saludplus

import com.flores.saludplus.data.model.Usuario
import com.flores.saludplus.data.repository.Repositorio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

// Relaciones:
// - Prueba sedes, horarios por médico, horariosDisponibles y agendarCita (data/repository/Repositorio.kt)
// - Usa AHORA_PRUEBA (Horas.kt), el modelo Usuario y JUnit 4

// Fase 3: cada médico atiende solo en sus días y horas; no se agenda en el pasado ni fuera de horario
class HorariosDeMedicoTest {

    @Before
    fun preparar() {
        Repositorio.citas.clear()
        Repositorio.usuarioActual = Usuario(99, "Paciente Prueba", "999999999", "prueba@correo.com", "abc123")
    }

    // Verifica que un médico que atiende martes y jueves por la tarde (2) no tiene horas un lunes
    @Test
    fun diaSinAtencion_noTieneHorarios() {
        assertTrue(Repositorio.horariosDisponibles(2, "2026-10-05", AHORA_PRUEBA).isEmpty())
    }

    // Verifica que ese mismo médico tiene de 15:00 a 18:30 un martes (8 horarios)
    @Test
    fun diaDeAtencion_tieneSusHoras() {
        val horas = Repositorio.horariosDisponibles(2, "2026-10-06", AHORA_PRUEBA)
        assertEquals("15:00", horas.first())
        assertEquals("18:30", horas.last())
        assertEquals(8, horas.size)
    }

    // Verifica que se puede agendar a las 5 de la tarde (17:00) y que luego esa hora desaparece
    @Test
    fun agendarALas17_ocultaEsaHora() {
        assertNotNull(Repositorio.agendarCita(2, "2026-10-06", "17:00", "", AHORA_PRUEBA))
        assertFalse("17:00" in Repositorio.horariosDisponibles(2, "2026-10-06", AHORA_PRUEBA))
    }

    // Verifica que no se puede agendar una hora fuera del horario del médico (08:00 con un médico de tarde)
    @Test
    fun horaFueraDeHorario_devuelveNull() {
        assertNull(Repositorio.agendarCita(2, "2026-10-06", "08:00", "", AHORA_PRUEBA))
    }

    // Verifica que no se puede agendar un día en que el médico no atiende
    @Test
    fun diaSinAtencion_devuelveNull() {
        assertNull(Repositorio.agendarCita(2, "2026-10-05", "16:00", "", AHORA_PRUEBA))
    }

    // Verifica que un día pasado no tiene horarios
    @Test
    fun diaPasado_noTieneHorarios() {
        assertTrue(Repositorio.horariosDisponibles(1, "2026-09-30", AHORA_PRUEBA).isEmpty())
    }

    // Verifica que hoy solo salen las horas que aún no pasaron (a las 10:00 ya no salen la de 09:30 ni la de 10:00)
    @Test
    fun hoy_quitaLasHorasQueYaPasaron() {
        val aLas10 = LocalDateTime.of(2026, 10, 1, 10, 0)
        // 2026-10-01 es jueves y el médico 1 atiende de lunes a viernes por la mañana
        val horas = Repositorio.horariosDisponibles(1, "2026-10-01", aLas10)
        assertEquals(listOf("10:30", "11:00", "11:30"), horas)
    }

    // Verifica que una fecha mal escrita o un médico inexistente no rompen y devuelven lista vacía
    @Test
    fun datosInvalidos_devuelvenListaVacia() {
        assertTrue(Repositorio.horariosDisponibles(1, "fecha-mala", AHORA_PRUEBA).isEmpty())
        assertTrue(Repositorio.horariosDisponibles(999, "2026-10-06", AHORA_PRUEBA).isEmpty())
    }

    // Verifica que sin sesión iniciada no se agenda
    @Test
    fun sinSesion_devuelveNull() {
        Repositorio.usuarioActual = null
        assertNull(Repositorio.agendarCita(1, "2026-10-06", "09:30", "", AHORA_PRUEBA))
    }

    // Verifica que cada médico pertenece a una sede que existe y tiene código, teléfono y horario
    @Test
    fun cadaMedico_tieneSedeCodigoYTelefono() {
        Repositorio.medicos.forEach { medico ->
            assertNotNull(Repositorio.obtenerSede(medico.sedeId))
            assertTrue(medico.codigo.isNotBlank())
            assertTrue(medico.telefono.isNotBlank())
            assertTrue(medico.horasMaximasPorDia > 0)
        }
    }

    // Verifica que los médicos se filtran por sede y que cada sede tiene varias especialidades
    @Test
    fun medicosPorSede_soloDevuelveLosDeEsaSede() {
        Repositorio.sedes.forEach { sede ->
            val especialidades = Repositorio.especialidadesDeSede(sede.id)
            assertTrue(especialidades.size > 1)
            especialidades.forEach { especialidad ->
                val lista = Repositorio.medicosPorEspecialidad(especialidad.id, sede.id)
                assertTrue(lista.isNotEmpty())
                assertTrue(lista.all { it.sedeId == sede.id })
            }
        }
        assertTrue(Repositorio.sedes.size >= 4)
    }

    // Verifica el texto del horario y la etiqueta de disponibilidad
    @Test
    fun resumenYEtiquetaDeDisponibilidad() {
        assertEquals("Mar y Jue · 15:00 a 19:00", Repositorio.resumenHorario(Repositorio.obtenerMedico(2)!!))
        // jueves 1 oct 08:00: el médico 1 atiende hoy por la mañana
        assertEquals("Disponible hoy", Repositorio.etiquetaDisponibilidad(1, AHORA_PRUEBA))
        // el médico 3 (lun/mié/vie por la tarde) atiende mañana (viernes)
        assertEquals("Disponible mañana", Repositorio.etiquetaDisponibilidad(3, AHORA_PRUEBA))
    }
}
