package com.flores.saludplus.ui.screens.resultados

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.model.Cita
import com.flores.saludplus.data.model.Resultado
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.components.BarraInferior
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.GrisMarcado
import com.flores.saludplus.ui.theme.Naranja
import com.flores.saludplus.ui.theme.NaranjaClaro
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible
import com.flores.saludplus.util.ESTADO_DISPONIBLE
import com.flores.saludplus.util.ESTADO_EN_PROCESO
import com.flores.saludplus.util.Fechas
import com.flores.saludplus.util.resultadosDeCita
import java.time.LocalDate

// Relaciones:
// - La llama AppNavigation en Rutas.RESULTADOS
// - Llama a BarraInferior (ui/components/Componentes.kt)
// - Fase 2: llama a Repositorio.citasDelUsuario, obtenerMedico y obtenerEspecialidad
// - Fase 2: usa resultadosDeCita (util/Examenes.kt), el modelo Resultado y util/Fechas.kt

// Commit 5: marco con barra inferior
// Fase 2: dibuja, por cada cita del paciente (de la más reciente a la más antigua), un encabezado
// con especialidad y médico seguido de las tarjetas de sus exámenes; sin citas, un mensaje vacío
@Composable
fun ResultadosScreen(onNavegar: (String) -> Unit) {
    // Fase 2: fecha de hoy para calcular el estado de cada examen
    val hoy = remember { LocalDate.now() }
    // Fase 2: citas de la más reciente a la más antigua
    val citas = Repositorio.citasDelUsuario()
        .sortedWith(compareByDescending<Cita> { it.fecha }.thenByDescending { it.hora })

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraInferior(Rutas.RESULTADOS, onNavegar) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            // Fase 2: título 34sp negrita azul marino a la izquierda
            Text(
                "Resultados",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            if (citas.isEmpty()) {
                EstadoSinResultados()
            } else {
                // Fase 2: lista que ocupa el alto libre y deja 24dp sobre la barra inferior
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    citas.forEach { cita ->
                        val medico = Repositorio.obtenerMedico(cita.medicoId)
                        val especialidadId = medico?.especialidadId ?: 0
                        val especialidad = Repositorio.obtenerEspecialidad(especialidadId)?.nombre ?: ""
                        // Fase 2: encabezado de la cita
                        item(key = "cita_${cita.id}") {
                            EncabezadoCita("$especialidad · ${medico?.nombre ?: ""}", Fechas.textoCortoDesdeIso(cita.fecha))
                        }
                        // Fase 2: exámenes de la especialidad con su estado
                        items(
                            resultadosDeCita(especialidadId, cita.fecha, hoy),
                            key = { "cita_${cita.id}_${it.id}" }
                        ) { resultado ->
                            TarjetaResultado(resultado)
                        }
                    }
                }
            }
        }
    }
}

// Fase 2: dibuja el encabezado de una cita: "Especialidad · Médico" (20sp negrita) y su fecha (17sp gris)
@Composable
private fun EncabezadoCita(titulo: String, fecha: String) {
    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        Text(fecha, fontSize = 17.sp, color = TextoSecundario)
    }
}

// Fase 2: dibuja la tarjeta de un examen: ícono de documento en círculo de 56dp,
// título 20sp negrita y la etiqueta del estado
@Composable
private fun TarjetaResultado(resultado: Resultado) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(AzulClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Description, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(30.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(resultado.titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            EtiquetaEstado(resultado.estado)
        }
    }
}

// Fase 2: dibuja la etiqueta del estado: verde claro "Disponible", naranja claro "En proceso"
// y gris claro "Pendiente"
@Composable
private fun EtiquetaEstado(estado: String) {
    val (fondo, texto) = when (estado) {
        ESTADO_DISPONIBLE -> VerdeClaro to VerdeDisponible
        ESTADO_EN_PROCESO -> NaranjaClaro to Naranja
        else -> Divisor to GrisMarcado
    }
    Box(
        modifier = Modifier
            .background(fondo, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(estado, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = texto)
    }
}

// Fase 2: dibuja el estado vacío centrado y desplazable cuando el paciente no tiene citas
@Composable
private fun EstadoSinResultados() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Aún no tienes resultados",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Tus resultados aparecerán después de tus citas",
                fontSize = 18.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
