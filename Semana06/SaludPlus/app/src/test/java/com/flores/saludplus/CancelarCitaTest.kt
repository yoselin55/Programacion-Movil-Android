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
// - Prueba Repositorio.cancelarCita, agendarCita y horariosDisponibles (data/repository/Repositorio.kt)
// - Usa el modelo Usuario (data/model/Usuario.kt) y JUnit 4

// Fase 2: pruebas de la cancelación de citas
class CancelarCitaTest {

    // Fase 2: deja el repositorio sin citas y con un usuario en sesión antes de cada prueba
    @Before
    fun preparar() {
        Repositorio.citas.clear()
        Repositorio.usuarioActual = Usuario(99, "Paciente Prueba", "999999999", "prueba@correo.com", "abc123")
    }

    // Verifica que al cancelar una cita su hora vuelve a aparecer para ese médico y fecha
    @Test
    fun cancelarCita_horaVuelveAEstarDisponible() {
        val cita = Repositorio.agendarCita(medicoId = 1, fecha = "2026-10-06", hora = "09:30", motivo = "", ahora = AHORA_PRUEBA)
        assertNotNull(cita)
        assertFalse("09:30" in Repositorio.horariosDisponibles(1, "2026-10-06", AHORA_PRUEBA))

        assertTrue(Repositorio.cancelarCita(cita!!.id))
        assertTrue("09:30" in Repositorio.horariosDisponibles(1, "2026-10-06", AHORA_PRUEBA))
    }

    // Verifica que la cita cancelada ya no se encuentra con obtenerCita
    @Test
    fun cancelarCita_yaNoSeEncuentra() {
        val cita = Repositorio.agendarCita(medicoId = 2, fecha = "2026-10-06", hora = "16:00", motivo = "Control", ahora = AHORA_PRUEBA)!!
        Repositorio.cancelarCita(cita.id)
        assertNull(Repositorio.obtenerCita(cita.id))
    }

    // Verifica que cancelar un id que no existe devuelve false
    @Test
    fun cancelarCita_idInexistente_devuelveFalse() {
        assertFalse(Repositorio.cancelarCita(12345))
    }
}
