package com.flores.firabase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.firabase.ui.theme.GradienteInicio

/** Pantalla de inicio: foto de fondo, logo, mensaje y botones para entrar o registrarse. */
@Composable
fun InicioScreen(onIniciarSesion: () -> Unit, onCrearCuenta: () -> Unit) {
    IconosBarraEstado(oscuros = false)

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_tecsup),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Oscurece la foto y la tiñe de rojo hacia abajo
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.10f),
                        0.40f to Color.Black.copy(alpha = 0.40f),
                        1f to GradienteInicio.copy(alpha = 0.97f)
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))
            LogoApp(rojo = true, tamano = 112.dp)

            Spacer(Modifier.weight(1f))
            Text(
                "Bienvenido/a",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .width(150.dp)
                    .height(3.dp)
                    .background(Color(0xFFE53935), RoundedCornerShape(50))
            )
            Spacer(Modifier.height(18.dp))
            Text(
                "Gestiona tus cursos en la nube de forma fácil, segura y organizada.",
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(36.dp))
            BotonPrincipal("Iniciar sesión", cargando = false, onClick = onIniciarSesion, flecha = true)
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onCrearCuenta,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.5.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Crear cuenta", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            }

            // Deja espacio libre debajo para que los botones queden más arriba
            Spacer(Modifier.weight(0.7f))
            Text(
                "Desarrollado por $AUTORA",
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
