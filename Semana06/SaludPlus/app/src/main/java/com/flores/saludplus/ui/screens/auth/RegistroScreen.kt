package com.flores.saludplus.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.CampoTexto
import com.flores.saludplus.ui.components.DialogoConfirmacion
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.GrisMarcado
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.Validaciones

// Relaciones:
// - La llama AppNavigation en el destino Rutas.REGISTRO
// - Llama a BotonPrincipal y CampoTexto (Componentes.kt; Fase 2: CampoTexto dibuja una CampoFila de 80dp:
//   recuadro de ícono de 78dp + etiqueta y caja blanca de 54dp)
// - Fase 2: valida nombre, teléfono, correo y contraseña con util/Validaciones.kt
// - Llama a Repositorio.registrarUsuario; si se registra va a Inicio (onRegistrado)
// - Sus enlaces llevan a Términos (onTerminos) y a Login (onIrLogin)

// Formulario de registro con validaciones de cada campo
// Fase 2: medidas tomadas del diseño de referencia (pantalla de 411 x 913 dp)
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
    // Fase 3: muestra el mensaje de confirmación una vez creada la cuenta
    var registroExitoso by rememberSaveable { mutableStateOf(false) }

    // Fase 2: nombre y correo sin espacios sobrantes (inicio, final y espacios repetidos)
    val nombreLimpio = nombre.trim().replace(Regex("\\s+"), " ")
    val correoLimpio = correo.trim()

    // Fase 2: reglas de util/Validaciones.kt; cada una devuelve el mensaje de error o null si está bien
    val reglaNombre = Validaciones.errorNombre(nombreLimpio)
    val reglaTelefono = Validaciones.errorTelefono(telefono)
    val reglaCorreo = Validaciones.errorCorreo(correoLimpio)
    val reglaContrasena = Validaciones.errorContrasena(contrasena)

    // Fase 2: el error de cada campo se muestra si el usuario ya escribió en él o pulsó "Registrarme"
    val errorNombre = reglaNombre.takeIf { intentoRegistrar || nombre.isNotEmpty() }
    val errorTelefono = when {
        telefonoRepetido -> "Este teléfono ya está registrado"
        intentoRegistrar || telefono.isNotEmpty() -> reglaTelefono
        else -> null
    }
    val errorCorreo = reglaCorreo.takeIf { intentoRegistrar || correo.isNotEmpty() }
    val errorContrasena = reglaContrasena.takeIf { intentoRegistrar || contrasena.isNotEmpty() }

    // Fase 3: confirmación con el logo; al continuar entra a Inicio
    if (registroExitoso) {
        DialogoConfirmacion(
            titulo = "¡Registro exitoso!",
            mensaje = "Tu cuenta fue creada, ${nombreLimpio.substringBefore(" ")}. Ya puedes agendar tus citas.",
            onContinuar = onRegistrado
        )
    }

    Scaffold(
        // Fase 2: fondo blanco
        containerColor = Color.White,
        // Fase 2: "¿Ya tienes cuenta? Iniciar sesión" anclado abajo con 40dp de margen inferior
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(top = 8.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes cuenta? ", fontSize = 20.sp, color = TextoPrincipal)
                Text(
                    "Iniciar sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable(onClick = onIrLogin)
                )
            }
        }
    ) { padding ->
        // Fase 2: contenido desplazable con 20dp de margen lateral; imePadding deja sitio al teclado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fase 2: 64dp bajo la barra de estado
            Spacer(Modifier.height(64.dp))

            // Fase 2: título 38sp y subtítulo 20sp, centrados
            Text("Crear cuenta", fontSize = 38.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Text(
                "Regístrate para agendar tus citas",
                fontSize = 20.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(36.dp))

            // Fase 2: cuatro filas de campo de 80dp con 26dp de separación
            Column(verticalArrangement = Arrangement.spacedBy(26.dp)) {
                // Fase 2: el nombre solo deja escribir letras, espacios y guion (máx. 40 caracteres)
                CampoTexto(
                    nombre,
                    { if (it.length <= Validaciones.MAX_NOMBRE && it.all { c -> c.isLetter() || c == ' ' || c == '-' }) nombre = it },
                    "Nombre completo", Icons.Filled.Person, error = errorNombre
                )
                // El teléfono solo deja escribir dígitos (máx. 9)
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
            }

            // Fase 2: botón de 72dp, esquinas 18dp y texto 24sp
            Spacer(Modifier.height(34.dp))
            BotonPrincipal(
                "Registrarme",
                alto = 72.dp,
                radio = 18.dp,
                tamanoTexto = 24.sp,
                onClick = {
                    intentoRegistrar = true
                    // Fase 2: no registra mientras alguna regla devuelva un error
                    val datosValidos = listOf(reglaNombre, reglaTelefono, reglaCorreo, reglaContrasena).all { it == null }
                    if (datosValidos) {
                        // Fase 2: guarda el nombre y el correo sin espacios sobrantes
                        if (Repositorio.registrarUsuario(nombreLimpio, telefono, correoLimpio, contrasena)) {
                            registroExitoso = true
                        } else {
                            telefonoRepetido = true
                        }
                    }
                }
            )

            // Fase 2: aviso de términos (18sp gris marcado) y enlace (20sp SemiBold azul)
            Spacer(Modifier.height(28.dp))
            Text("Al registrarte aceptas nuestros", fontSize = 18.sp, color = GrisMarcado, textAlign = TextAlign.Center)
            Text(
                "Términos y Condiciones",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrimario,
                modifier = Modifier.clickable(onClick = onTerminos).padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
