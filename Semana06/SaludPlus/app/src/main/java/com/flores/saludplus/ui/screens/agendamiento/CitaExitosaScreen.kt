package com.flores.saludplus.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.FilaDetalle
import com.flores.saludplus.ui.components.FotoMedico
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.BordeSuave
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible
import com.flores.saludplus.util.Fechas

// Relaciones:
// - La llama AppNavigation en Rutas.CITA_EXITOSA y recibe citaId de la ruta
// - Usa BotonPrincipal y FilaDetalle (Componentes.kt); Fase 2: también FotoMedico (ImagenPorNombre.kt)
// - Llama a Repositorio.obtenerCita, obtenerMedico y obtenerEspecialidad
// - Usa util/Fechas.kt (Fase 2) para la fecha en español y el rango de hora (rangoHora)
// - Sus botones llevan a Mis citas (onVerMisCitas) y a Inicio (onIrInicio)

// Commit 8: confirmación con el resumen de la cita agendada
// Fase 2: círculo verde de 120dp con check, tarjeta de resumen con foto y botones grandes
@Composable
fun CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onIrInicio: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId)?.nombre } ?: ""

    Scaffold(containerColor = Color.White) { padding ->
        // Fase 2: contenido centrado verticalmente si cabe y desplazable si no cabe
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Fase 2: círculo verde claro de 120dp con check verde de 88dp
                Box(
                    modifier = Modifier.size(120.dp).background(VerdeClaro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = VerdeDisponible, modifier = Modifier.size(88.dp))
                }
                Spacer(Modifier.height(20.dp))
                // Fase 2: título 34sp negrita azul marino y subtítulo 20sp gris
                Text(
                    "¡Cita agendada!",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    textAlign = TextAlign.Center
                )
                Text("Te esperamos en la clínica", fontSize = 20.sp, color = TextoSecundario, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))

                // Resumen de la cita
                if (cita != null && medico != null) {
                    // Fase 2: tarjeta con borde 1dp, esquinas 20dp y padding 16dp
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(1.dp, BordeSuave, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        // Fase 2: foto de 72dp con nombre 22sp negrita y especialidad 18sp gris
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            FotoMedico(medico.nombre, 72.dp)
                            Column {
                                Text(medico.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                                Text(especialidad, fontSize = 18.sp, color = TextoSecundario)
                            }
                        }
                        // Fase 2: fecha, hora (mismo rango que Confirmar cita) y dirección, con líneas de 1dp
                        val datos = listOf(
                            Triple(Icons.Filled.CalendarMonth, "Fecha", Fechas.textoLargoDesdeIso(cita.fecha)),
                            Triple(Icons.Filled.AccessTime, "Hora", Fechas.rangoHora(cita.hora)),
                            Triple(Icons.Filled.LocationOn, "Dirección", "Av. Los Olivos 123, Lima")
                        )
                        datos.forEach { (icono, titulo, valor) ->
                            HorizontalDivider(thickness = 1.dp, color = Divisor)
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                FilaDetalle(icono, titulo, valor, tamanoTitulo = 18.sp, tamanoValor = 22.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                // Fase 2: botón principal de 68dp y enlace "Ir al inicio" de 22sp SemiBold azul
                BotonPrincipal("Ver mis citas", onClick = onVerMisCitas, alto = 68.dp, radio = 18.dp, tamanoTexto = 22.sp)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onIrInicio) {
                    Text("Ir al inicio", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = AzulPrimario)
                }
            }
        }
    }
}
