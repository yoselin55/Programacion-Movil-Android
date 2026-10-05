package com.flores.saludplus.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.LogoClinica
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario

// Relaciones:
// - La llama AppNavigation en el destino Rutas.SPLASH
// - Llama a LogoClinica y BotonPrincipal (ui/components/Componentes.kt)
// - Sus botones llevan a Registro (onComenzar) y a Login (onYaTengoCuenta)

// Pantalla de bienvenida: logo, lema, ilustración y botones de entrada
@Composable
fun SplashScreen(onComenzar: () -> Unit, onYaTengoCuenta: () -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            LogoClinica()
            Spacer(Modifier.height(16.dp))
            Text("Clínica", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Text("SaludPlus", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = AzulPrimario)
            Spacer(Modifier.height(8.dp))
            Text(
                "Tu salud, nuestra prioridad",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Ilustración central (círculo claro con ícono médico)
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(200.dp).background(AzulClaro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.MedicalServices,
                        contentDescription = null,
                        tint = AzulPrimario,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonPrincipal("Comenzar", onClick = onComenzar)
                TextButton(onClick = onYaTengoCuenta) { Text("Ya tengo una cuenta") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
