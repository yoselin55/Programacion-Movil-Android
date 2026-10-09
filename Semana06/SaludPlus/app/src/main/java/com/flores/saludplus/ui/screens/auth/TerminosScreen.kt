package com.flores.saludplus.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.TextoPrincipal

// Relaciones:
// - La llama AppNavigation en el destino Rutas.TERMINOS (enlace de RegistroScreen)
// - Fase 2: usa BarraSuperior y BotonPrincipal (Componentes.kt)
// - No usa el Repositorio

// Fase 2: secciones de los términos (título y texto)
private val secciones = listOf(
    "Uso de la aplicación" to
        "SaludPlus permite a los pacientes de la Clínica SaludPlus agendar, revisar y cancelar citas " +
        "médicas. La aplicación es de uso personal y no reemplaza la atención de emergencia.",
    "Datos personales" to
        "Tu nombre, teléfono y correo se usan solo para identificarte y gestionar tus citas. " +
        "No se comparten con terceros y puedes cerrar tu sesión cuando quieras.",
    "Citas médicas" to
        "Cada cita dura 30 minutos y se agenda de lunes a viernes según la disponibilidad del médico. " +
        "Te recomendamos llegar 15 minutos antes con tu documento de identidad.",
    "Cancelaciones y reprogramaciones" to
        "Puedes cancelar una cita desde Mis citas; el horario queda libre para otros pacientes. " +
        "Para cambiar de fecha, cancela la cita y agenda una nueva.",
    "Responsabilidad" to
        "La información de la aplicación es referencial. Los diagnósticos y tratamientos los define " +
        "el médico durante la consulta. Mantén tu contraseña en reserva.",
    "Contacto" to
        "Si tienes dudas sobre estos términos o sobre tus citas, acércate al módulo de atención al " +
        "paciente de la clínica."
)

// Fase 2: dibuja los términos y condiciones desplazables y el botón fijo "Entendido" que vuelve atrás
@Composable
fun TerminosScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior("Términos y condiciones", onBack) },
        // Fase 2: botón fijo abajo de 68dp
        bottomBar = {
            BotonPrincipal(
                "Entendido",
                onClick = onBack,
                alto = 68.dp,
                radio = 18.dp,
                tamanoTexto = 22.sp,
                modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
    ) { padding ->
        // Fase 2: contenido desplazable con 20dp de margen lateral
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            secciones.forEach { (titulo, texto) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Fase 2: título 20sp negrita azul marino y texto 18sp
                    Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Text(texto, fontSize = 18.sp, lineHeight = 26.sp, color = TextoPrincipal)
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}
