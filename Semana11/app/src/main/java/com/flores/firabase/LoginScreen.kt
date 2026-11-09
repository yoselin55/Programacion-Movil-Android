package com.flores.firabase

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit,
    onIrRegistro: () -> Unit,
    onVolver: () -> Unit
) {
    IconosBarraEstado(oscuros = !isSystemInDarkTheme())

    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun ingresar() {
        if (email.isBlank() || password.isBlank()) {
            error = "Completa tu correo y contraseña."
            return
        }
        cargando = true
        error = null
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                cargando = false
                if (task.isSuccessful) onLoginOk() else error = mensajeAuth(task.exception)
            }
    }

    FondoOndas {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                BotonVolver(onClick = onVolver, modifier = Modifier.align(Alignment.CenterStart))
            }
            Spacer(Modifier.height(8.dp))
            LogoApp()
            Spacer(Modifier.height(20.dp))
            Text(
                "Inicia sesión",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Accede para gestionar tus cursos",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(32.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CampoTexto(
                    valor = email,
                    onCambio = { email = it },
                    etiqueta = "Correo electrónico",
                    icono = R.drawable.ic_email,
                    tipo = KeyboardType.Email
                )
                CampoTexto(
                    valor = password,
                    onCambio = { password = it },
                    etiqueta = "Contraseña",
                    icono = R.drawable.ic_lock,
                    esClave = true,
                    imeAction = ImeAction.Done,
                    onAccion = { ingresar() }
                )
                error?.let { MensajeError(it) }
                Spacer(Modifier.height(4.dp))
                BotonPrincipal("Ingresar", cargando, onClick = { ingresar() })
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "¿No tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onIrRegistro) {
                    Text(
                        "Regístrate",
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
