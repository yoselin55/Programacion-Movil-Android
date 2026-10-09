package com.flores.saludplus

import com.flores.saludplus.data.model.Usuario
import com.flores.saludplus.data.repository.Repositorio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

// Relaciones:
// - Prueba Repositorio.agendarCita y citaDelUsuarioEn (data/repository/Repositorio.kt)
// - Usa el modelo Usuario (data/model/Usuario.kt) y JUnit 4

// Fase 2: el paciente no puede tener dos citas a la misma fecha y hora
class CrucesDeCitasTest {

    // Fase 2: deja el repositorio sin citas y con un usuario en sesión antes de cada prueba
    @Before
    fun preparar() {
        Repositorio.citas.clear()
        Repositorio.usuarioActual = Usuario(99, "Paciente Prueba", "999999999", "prueba@correo.com", "abc123")
    }

    // Verifica que no se puede agendar con otro médico (10) a la misma fecha y hora que una cita existente (médico 9)
    @Test
    fun mismaFechaYHora_otroMedico_devuelveNull() {
        assertNotNull(Repositorio.agendarCita(medicoId = 9, fecha = "2026-10-09", hora = "15:30", motivo = "", ahora = AHORA_PRUEBA))
        assertNull(Repositorio.agendarCita(medicoId = 10, fecha = "2026-10-09", hora = "15:30", motivo = "", ahora = AHORA_PRUEBA))
    }

    // Verifica que citaDelUsuarioEn devuelve la primera cita del paciente en esa fecha y hora
    @Test
    fun citaDelUsuarioEn_devuelveLaCitaExistente() {
        val primera = Repositorio.agendarCita(medicoId = 9, fecha = "2026-10-09", hora = "15:30", motivo = "", ahora = AHORA_PRUEBA)
        Repositorio.agendarCita(medicoId = 10, fecha = "2026-10-09", hora = "15:30", motivo = "", ahora = AHORA_PRUEBA)
        assertEquals(primera, Repositorio.citaDelUsuarioEn("2026-10-09", "15:30"))
    }

    // Verifica que a otra hora del mismo día (16:00) sí se puede agendar con otro médico
    @Test
    fun mismaFechaOtraHora_siSePuedeAgendar() {
        Repositorio.agendarCita(medicoId = 9, fecha = "2026-10-09", hora = "15:30", motivo = "", ahora = AHORA_PRUEBA)
        assertNotNull(Repositorio.agendarCita(medicoId = 10, fecha = "2026-10-09", hora = "16:00", motivo = "", ahora = AHORA_PRUEBA))
    }

    // Verifica que citaDelUsuarioEn devuelve null cuando no hay cita a esa fecha y hora
    @Test
    fun citaDelUsuarioEn_sinCita_devuelveNull() {
        assertNull(Repositorio.citaDelUsuarioEn("2026-10-09", "15:30"))
    }
}
