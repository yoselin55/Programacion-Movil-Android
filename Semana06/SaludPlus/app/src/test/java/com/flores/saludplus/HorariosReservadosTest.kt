package com.flores.saludplus

import com.flores.saludplus.data.model.Usuario
import com.flores.saludplus.data.repository.Repositorio
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Relaciones:
// - Prueba Repositorio.agendarCita y horariosDisponibles (data/repository/Repositorio.kt)
// - Usa el modelo Usuario (data/model/Usuario.kt) y JUnit 4

// Fase 2: verifica que el calendario dinámico no rompe el bloqueo de horarios reservados
class HorariosReservadosTest {

    // Fase 2: deja el repositorio sin citas y con un usuario en sesión antes de cada prueba
    @Before
    fun preparar() {
        Repositorio.citas.clear()
        Repositorio.usuarioActual = Usuario(99, "Paciente Prueba", "999999999", "prueba@correo.com", "1234")
    }

    // Verifica que una hora agendada ya no aparece para ese médico en esa fecha
    @Test
    fun horaAgendada_noApareceEnHorariosDisponibles() {
        assertNotNull(Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = ""))
        assertFalse("09:30" in Repositorio.horariosDisponibles(1, "2026-10-06"))
    }

    // Verifica que la misma hora sigue libre para otro médico (2) en la misma fecha
    @Test
    fun horaAgendada_sigueDisponibleParaOtroMedico() {
        Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = "")
        assertTrue("09:30" in Repositorio.horariosDisponibles(2, "2026-10-06"))
    }

    // Verifica que la misma hora sigue libre para el mismo médico en otra fecha
    @Test
    fun horaAgendada_sigueDisponibleEnOtraFecha() {
        Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = "")
        assertTrue("09:30" in Repositorio.horariosDisponibles(1, "2026-10-07"))
    }

    // Verifica que agendar de nuevo el mismo médico, fecha y hora devuelve null
    @Test
    fun agendarDosVeces_mismoHorario_devuelveNull() {
        Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = "")
        assertNull(Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = ""))
    }
}
