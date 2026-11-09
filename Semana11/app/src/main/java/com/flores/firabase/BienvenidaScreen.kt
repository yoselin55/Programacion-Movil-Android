package com.flores.firabase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.firabase.ui.theme.GradienteFin
import com.flores.firabase.ui.theme.GradienteInicio
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

/** Pantalla que aparece después de iniciar sesión o registrarse, antes de entrar a la app. */
@Composable
fun BienvenidaScreen(onContinuar: () -> Unit) {
    IconosBarraEstado(oscuros = false)

    val usuario = FirebaseAuth.getInstance().currentUser
    val correo = usuario?.email.orEmpty()
    val nombre = (usuario?.displayName ?: "").ifBlank { correo.substringBefore("@") }
        .ifBlank { "Estudiante" }
        .replaceFirstChar { it.uppercase() }

    // Entra sola a la app después de unos segundos
    LaunchedEffect(Unit) {
        delay(3500)
        onContinuar()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradienteInicio, GradienteFin)))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .border(5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "¡Bienvenida/o!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(6.dp))
        Text(
            nombre,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Tu sesión se inició correctamente.\nPreparando tus cursos...",
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(28.dp))
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(50)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.25f)
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onContinuar,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = GradienteInicio
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Entrar a Mis Cursos", fontWeight = FontWeight.SemiBold)
        }
    }
}
