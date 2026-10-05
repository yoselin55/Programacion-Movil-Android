package com.flores.saludplus.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.CampoTexto

// Relaciones:
// - La llama AppNavigation en el destino Rutas.LOGIN
// - Llama a BarraSuperior, BotonPrincipal y CampoTexto (ui/components/Componentes.kt)
// - Llama a Repositorio.iniciarSesion; si coincide va a Inicio (onLoginExitoso)
// - Su enlace lleva a Registro (onIrRegistro)

// Inicio de sesión contra la lista de usuarios registrados
@Composable
fun LoginScreen(onLoginExitoso: () -> Unit, onIrRegistro: () -> Unit, onBack: () -> Unit) {
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var intentoIngresar by rememberSaveable { mutableStateOf(false) }
    var credencialesIncorrectas by rememberSaveable { mutableStateOf(false) }

    // Validaciones de campos vacíos o con formato incorrecto
    val errorTelefono = if (intentoIngresar && telefono.length != 9) "El teléfono debe tener 9 dígitos" else null
    val errorContrasena = if (intentoIngresar && contrasena.isBlank()) "Ingresa tu contraseña" else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior("Iniciar sesión", onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Bienvenido de nuevo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "Ingresa con tu teléfono y contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            CampoTexto(
                telefono,
                { if (it.length <= 9 && it.all(Char::isDigit)) { telefono = it; credencialesIncorrectas = false } },
                "Teléfono", Icons.Filled.Phone, error = errorTelefono, teclado = KeyboardType.Phone
            )
            CampoTexto(
                contrasena, { contrasena = it; credencialesIncorrectas = false },
                "Contraseña", Icons.Filled.Lock,
                error = errorContrasena, teclado = KeyboardType.Password, esContrasena = true
            )

            if (credencialesIncorrectas) {
                Text("Teléfono o contraseña incorrectos", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Ingresar", onClick = {
                intentoIngresar = true
                if (telefono.length == 9 && contrasena.isNotBlank()) {
                    if (Repositorio.iniciarSesion(telefono, contrasena)) onLoginExitoso()
                    else credencialesIncorrectas = true
                }
            })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onIrRegistro) { Text("Regístrate") }
            }
        }
    }
}
