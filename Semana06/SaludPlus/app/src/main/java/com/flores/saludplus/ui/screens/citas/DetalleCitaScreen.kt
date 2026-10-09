package com.flores.saludplus.ui.screens.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonContornoRojo
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.components.FotoMedico
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.LineaSeparadora
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.Fechas

// Relaciones:
// - La llama AppNavigation en el destino Rutas.DETALLE_CITA y recibe citaId de la ruta
// - Fase 2: usa BarraSuperior, BotonPrincipal, BotonContornoRojo y FilaDetalle (Componentes.kt)
//   y FotoMedico (ImagenPorNombre.kt)
// - Fase 2: llama a Repositorio.obtenerCita, obtenerMedico, obtenerEspecialidad y cancelarCita
// - Fase 2: usa util/Fechas.kt (textoLargoDesdeIso y rangoHora)
// - Al cancelar o al volver regresa a Mis citas (onBack)

// Fase 2: dibuja el detalle de una cita (médico, fecha, hora, tipo, dirección y motivo) con el botón
// "Cancelar cita", que pide confirmación en un AlertDialog antes de eliminarla
@Composable
fun DetalleCitaScreen(citaId: Int, onBack: () -> Unit) {
    // Fase 2: la cita y su médico se leen una vez por id
    val cita = remember(citaId) { Repositorio.obtenerCita(citaId) }
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""
    // Fase 3: sede del médico (su nombre y dirección reemplazan a la dirección fija)
    val sede = medico?.let { Repositorio.obtenerSede(it.sedeId) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior("Detalle de cita", onBack) }
    ) { padding ->
        if (cita == null) {
            // Fase 2: la cita ya fue cancelada o el id no existe
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Esta cita ya no existe",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    textAlign = TextAlign.Center
                )
                BotonPrincipal("Volver", onClick = onBack, alto = 64.dp, radio = 16.dp, tamanoTexto = 22.sp)
            }
            return@Scaffold
        }

        // Fase 2: arriba el contenido desplazable y abajo, fijo, el botón "Cancelar cita"
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(8.dp))

                // Fase 2: tarjeta del médico: fondo azul claro, esquinas 20dp, padding 16dp y foto de 84dp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulClaro, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FotoMedico(medico?.nombre ?: "", 84.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        // Fase 2: nombre 20sp negrita y especialidad 18sp gris
                        Text(medico?.nombre ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        Text(especialidad, fontSize = 18.sp, color = TextoSecundario)
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Fase 2: datos de la cita; después de cada fila va una línea de 1.5dp
                val datos = listOf(
                    Triple(Icons.Filled.CalendarMonth, "Fecha", Fechas.textoLargoDesdeIso(cita.fecha)),
                    Triple(Icons.Filled.AccessTime, "Hora", Fechas.rangoHora(cita.hora)),
                    Triple(Icons.Filled.Info, "Tipo de atención", "Consulta presencial"),
                    Triple(Icons.Filled.LocationOn, "Sede ${sede?.nombre ?: ""}", sede?.direccion ?: ""),
                    Triple(Icons.Filled.EditNote, "Motivo de consulta", cita.motivo.ifBlank { "Sin motivo" })
                )
                datos.forEach { (icono, titulo, valor) ->
                    Column(modifier = Modifier.padding(vertical = 14.dp)) {
                        // Fase 2: título 18sp y valor 22sp
                        FilaDetalle(icono, titulo, valor, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                    }
                    HorizontalDivider(thickness = 1.5.dp, color = LineaSeparadora)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Fase 2: botón rojo de contorno (64dp, esquinas 16dp, borde 1.5dp, texto 22sp SemiBold)
            BotonContornoRojo(
                "Cancelar cita",
                onClick = { mostrarDialogo = true },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }

    // Fase 2: confirmación antes de cancelar; si acepta elimina la cita y vuelve a Mis citas
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Cancelar esta cita?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro) },
            text = { Text("El horario quedará libre para otros pacientes.", fontSize = 18.sp) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    Repositorio.cancelarCita(citaId)
                    onBack()
                }) {
                    Text("Sí, cancelar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RojoAviso)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("No", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White
        )
    }
}
