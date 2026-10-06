package com.flores.saludplus

import com.flores.saludplus.ui.components.nombreRecurso
import org.junit.Assert.assertEquals
import org.junit.Test

// Relaciones:
// - Prueba nombreRecurso (ui/components/ImagenPorNombre.kt)
// - Usa JUnit 4

// Fase 2: pruebas de la conversión de textos de la interfaz a nombres de recurso
class NombreRecursoTest {

    // Verifica que los espacios se cambian por "_" y todo queda en minúsculas
    @Test
    fun especialidadConEspacio() {
        assertEquals("medicina_general", nombreRecurso("Medicina General"))
    }

    // Verifica que se quitan las tildes
    @Test
    fun especialidadConTilde() {
        assertEquals("pediatria", nombreRecurso("Pediatría"))
    }

    // Verifica que se quita el punto del título "Dra." sin dejar "_" repetidos
    @Test
    fun medicoConPunto() {
        assertEquals("dra_ana_torres", nombreRecurso("Dra. Ana Torres"))
    }

    // Verifica que la ñ se convierte en n y la ú en u
    @Test
    fun medicoConEnieYTilde() {
        assertEquals("dr_ricardo_nunez", nombreRecurso("Dr. Ricardo Núñez"))
    }

    // Verifica que no quedan "_" al inicio ni al final
    @Test
    fun sinGuionesBajosEnLosBordes() {
        assertEquals("oftalmologia", nombreRecurso("  Oftalmología. "))
    }
}
