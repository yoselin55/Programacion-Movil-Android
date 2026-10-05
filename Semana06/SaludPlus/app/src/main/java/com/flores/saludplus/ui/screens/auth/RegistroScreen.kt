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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.CampoTexto

// Relaciones:
// - La llama AppNavigation en el destino Rutas.REGISTRO
// - Llama a BotonPrincipal y CampoTexto (ui/components/Componentes.kt)
// - Llama a Repositorio.registrarUsuario; si se registra va a Inicio (onRegistrado)
// - Sus enlaces llevan a Términos (onTerminos) y a Login (onIrLogin)

// Formulario de registro con validaciones de cada campo
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onTerminos: () -> Unit,
    onIrLogin: () -> Unit,
    onBack: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var intentoRegistrar by rememberSaveable { mutableStateOf(false) } // los errores salen al pulsar el botón
    var telefonoRepetido by rememberSaveable { mutableStateOf(false) }

    // Validaciones: cada una devuelve el mensaje de error o null si está bien
    val errorNombre = if (intentoRegistrar && nombre.isBlank()) "Ingresa tu nombre completo" else null
    val errorTelefono = when {
        telefonoRepetido -> "Este teléfono ya está registrado"
        intentoRegistrar && telefono.length != 9 -> "El teléfono debe tener 9 dígitos"
        else -> null
    }
    val errorCorreo = if (intentoRegistrar && correo.isNotBlank() &&
        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    ) "Correo no válido" else null
    val errorContrasena = if (intentoRegistrar && contrasena.length < 6) "Mínimo 6 caracteres" else null

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Regístrate para agendar tus citas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            CampoTexto(nombre, { nombre = it }, "Nombre completo", Icons.Filled.Person, error = errorNombre)
            CampoTexto(
                telefono,
                { if (it.length <= 9 && it.all(Char::isDigit)) { telefono = it; telefonoRepetido = false } },
                "Teléfono", Icons.Filled.Phone, error = errorTelefono, teclado = KeyboardType.Phone
            )
            CampoTexto(
                correo, { correo = it }, "Correo (opcional)", Icons.Filled.Email,
                error = errorCorreo, teclado = KeyboardType.Email
            )
            CampoTexto(
                contrasena, { contrasena = it }, "Contraseña", Icons.Filled.Lock,
                error = errorContrasena, teclado = KeyboardType.Password, esContrasena = true
            )

            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Registrarme", onClick = {
                intentoRegistrar = true
                val datosValidos = nombre.isNotBlank() && telefono.length == 9 &&
                    (correo.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) &&
                    contrasena.length >= 6
                if (datosValidos) {
                    if (Repositorio.registrarUsuario(nombre.trim(), telefono, correo.trim(), contrasena)) {
                        onRegistrado()
                    } else {
                        telefonoRepetido = true
                    }
                }
            })

            Text(
                "Al registrarte aceptas nuestros",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onTerminos) { Text("Términos y Condiciones") }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onIrLogin) { Text("Iniciar sesión") }
            }
        }
    }
}
