package com.flores.saludplus.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.ImagenPorNombre
import com.flores.saludplus.ui.components.LogoClinica
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.CelesteSplash
import com.flores.saludplus.ui.theme.TextoSecundario

// Relaciones:
// - La llama AppNavigation en el destino Rutas.SPLASH
// - Llama a BotonPrincipal y LogoClinica (Componentes.kt) e ImagenPorNombre (ImagenPorNombre.kt)
// - Fase 2: dibuja res/drawable/logo_saludplus e ilustracion_doctor por nombre
// - Sus botones llevan a Registro (onComenzar) y a Login (onYaTengoCuenta)

// Fase 2: proporción real de ilustracion_doctor.png (292 x 267 px)
private const val PROPORCION_DOCTOR = 292f / 267f

// Pantalla de bienvenida: logo, lema, ilustración y botones de entrada
// Fase 2: medidas tomadas del diseño de referencia (pantalla de 411 x 913 dp)
@Composable
fun SplashScreen(onComenzar: () -> Unit, onYaTengoCuenta: () -> Unit) {
    // Fase 2: fondo a pantalla completa con degradado celeste (arriba) -> blanco (abajo);
    // systemBarsPadding deja libres la barra de estado y la de navegación
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CelesteSplash, Color.White)))
            .systemBarsPadding()
    ) {
        // Fase 2: la columna mide al menos el alto de la pantalla (el Spacer con weight empuja la
        // ilustración y el botón hacia abajo). Si en una pantalla baja no cabe todo, se desplaza
        // en vez de recortar la imagen o achicar las fuentes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fase 2: 40dp bajo la barra de estado
            Spacer(Modifier.height(40.dp))

            // Fase 2: logo de 150dp de ancho (alto proporcional ~137dp)
            ImagenPorNombre(
                nombre = "logo_saludplus",
                descripcion = "Logo SaludPlus",
                modifier = Modifier.width(150.dp),
                contentScale = ContentScale.Fit,
                respaldo = { LogoClinica(150) }
            )
            Spacer(Modifier.height(8.dp))

            // Fase 2: "Clínica" 46sp y "SaludPlus" 58sp con las líneas pegadas (lineHeight 58sp)
            Text("Clínica", fontSize = 46.sp, lineHeight = 50.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Text("SaludPlus", fontSize = 58.sp, lineHeight = 58.sp, fontWeight = FontWeight.ExtraBold, color = AzulOscuro)
            Spacer(Modifier.height(6.dp))
            // Fase 2: lema de 24sp en gris
            Text("Tu salud, nuestra prioridad", fontSize = 24.sp, color = TextoSecundario)

            // Fase 2: espacio libre flexible; deja la ilustración pegada encima del botón
            Spacer(Modifier.weight(1f))

            // Fase 2: ilustración de borde a borde (sin padding), COMPLETA: el alto sale de su
            // proporción real (aspectRatio), así nunca se recorta la cabeza ni el cabello
            ImagenPorNombre(
                nombre = "ilustracion_doctor",
                descripcion = "Doctor de la clínica",
                modifier = Modifier.fillMaxWidth().aspectRatio(PROPORCION_DOCTOR),
                contentScale = ContentScale.FillWidth,
                respaldo = {}
            )

            // Fase 2: botón "Comenzar" con 20dp de margen lateral, 68dp de alto, esquinas 16dp y texto 22sp
            BotonPrincipal(
                "Comenzar",
                onClick = onComenzar,
                modifier = Modifier.padding(horizontal = 20.dp),
                alto = 68.dp,
                radio = 16.dp,
                tamanoTexto = 22.sp
            )
            Spacer(Modifier.height(12.dp))
            // Fase 2: enlace "Ya tengo una cuenta" 22sp SemiBold azul, centrado
            Text(
                "Ya tengo una cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrimario,
                modifier = Modifier.clickable(onClick = onYaTengoCuenta).padding(vertical = 4.dp, horizontal = 8.dp)
            )
            // Fase 2: margen inferior de 16dp
            Spacer(Modifier.height(16.dp))
        }
    }
}
