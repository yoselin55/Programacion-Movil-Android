package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.LogoLavaCampus

// Pantalla de carga mientras se revisa si ya hay un perfil guardado
@Composable
fun SplashScreen() {
    FotoConDegradado(R.drawable.foto_lavanderia, Modifier.fillMaxSize(), intensidad = 0.92f) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoLavaCampus(170.dp)
            Spacer(Modifier.height(22.dp))
            Text("LavaCampus", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Tu lavandería en un solo lugar", fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f))
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(28.dp))
        }
    }
}
