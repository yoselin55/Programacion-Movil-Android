package com.flores.saludplus

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.flores.saludplus.data.model.Usuario
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.screens.citas.MisCitasScreen
import com.flores.saludplus.ui.theme.SaludplusTheme
import org.junit.Rule
import org.junit.Test

// Relaciones:
// - Prueba MisCitasScreen (ui/screens/citas) en un dispositivo o emulador
// - Usa Repositorio.agendarCita para crear las citas y el modelo Usuario

// Fase 2: comprueba que la lista de Mis citas se desplaza con el dedo hasta la última cita
class MisCitasScrollTest {

    @get:Rule
    val regla = createComposeRule()

    // Verifica que con 12 citas, deslizando hacia arriba, se llega a ver la última (Dra. Elena Campos)
    @Test
    fun conDoceCitas_deslizarHastaLaUltima() {
        Repositorio.citas.clear()
        Repositorio.usuarioActual = Usuario(99, "Paciente Prueba", "999999999", "", "abc123")
        // Una cita por médico (1 a 12) en fechas seguidas; la del médico 12 queda al final
        for (medicoId in 1..12) {
            Repositorio.agendarCita(medicoId, "2026-11-%02d".format(medicoId), "08:00", "")
        }

        regla.setContent { SaludplusTheme { MisCitasScreen(onDetalle = {}, onNavegar = {}) } }

        // Desliza con el dedo varias veces, como lo haría el usuario
        repeat(6) {
            regla.onNode(hasScrollAction()).performTouchInput { swipeUp() }
        }
        regla.onNodeWithText("Dra. Elena Campos").assertIsDisplayed()
    }
}
