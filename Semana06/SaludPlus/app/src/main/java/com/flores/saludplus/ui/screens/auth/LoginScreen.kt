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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
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
import com.flores.saludplus.ui.components.BarraSuperior
import com.flores.saludplus.ui.components.BotonPrincipal
import com.flores.saludplus.ui.components.CampoTexto
import com.flores.saludplus.ui.components.DialogoConfirmacion
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.Validaciones

// Relaciones:
// - Fase 2: valida el teléfono con util/Validaciones.kt (errorTelefono)
// - La llama AppNavigation en el destino Rutas.LOGIN
// - Llama a BarraSuperior, BotonPrincipal y CampoTexto (ui/components/Componentes.kt)
// - Fase 2: sigue el estilo de RegistroScreen (fondo blanco, filas de 80dp con recuadro de 78dp y caja de 54dp, botón de 72dp)
// - Llama a Repositorio.iniciarSesion; si coincide va a Inicio (onLoginExitoso)
// - Su enlace lleva a Registro (onIrRegistro)

// Inicio de sesión contra la lista de usuarios registrados
@Composable
fun LoginScreen(onLoginExitoso: () -> Unit, onIrRegistro: () -> Unit, onBack: () -> Unit) {
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var intentoIngresar by rememberSaveable { mutableStateOf(false) }
    var credencialesIncorrectas by rememberSaveable { mutableStateOf(false) }
    // Fase 3: muestra el mensaje de confirmación una vez iniciada la sesión
    var sesionIniciada by rememberSaveable { mutableStateOf(false) }

    // Fase 2: valida el teléfono con util/Validaciones.kt (9 dígitos que empiezan con 9)
    val reglaTelefono = Validaciones.errorTelefono(telefono)
    // Fase 2: valida que la contraseña no esté vacía
    val reglaContrasena = if (contrasena.isEmpty()) "Ingresa tu contraseña" else null

    // Fase 2: el error del teléfono sale al escribir o al pulsar "Ingresar"; el de la contraseña al pulsar
    val errorTelefono = reglaTelefono.takeIf { intentoIngresar || telefono.isNotEmpty() }
    val errorContrasena = reglaContrasena.takeIf { intentoIngresar }

    // Fase 3: confirmación con el logo; al continuar entra a Inicio
    if (sesionIniciada) {
        DialogoConfirmacion(
            titulo = "¡Bienvenido!",
            mensaje = "Iniciaste sesión correctamente, ${Repositorio.usuarioActual?.nombre?.substringBefore(" ") ?: ""}.",
            onContinuar = onLoginExitoso
        )
    }

    Scaffold(
        // Fase 2: fondo blanco; BarraSuperior trae fondo blanco, flecha y título de 26sp negrita azul marino
        containerColor = Color.White,
        topBar = { BarraSuperior("Iniciar sesión", onBack) },
        // Fase 2: "¿No tienes cuenta? Regístrate" anclado abajo con 40dp de margen, como en Registro
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(top = 8.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta? ", fontSize = 20.sp, color = TextoPrincipal)
                Text(
                    "Regístrate",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable(onClick = onIrRegistro)
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
            Spacer(Modifier.height(32.dp))

            // Fase 2: título 34sp negrita y subtítulo 20sp gris, centrados
            Text("Bienvenido de nuevo", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = AzulOscuro, textAlign = TextAlign.Center)
            Text(
                "Ingresa con tu teléfono y contraseña",
                fontSize = 20.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(36.dp))

            // Fase 2: filas de campo de 80dp con 26dp de separación (mismo estilo que Registro)
            Column(verticalArrangement = Arrangement.spacedBy(26.dp)) {
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
            }

            if (credencialesIncorrectas) {
                Spacer(Modifier.height(16.dp))
                Text("Teléfono o contraseña incorrectos", fontSize = 15.sp, color = MaterialTheme.colorScheme.error)
            }

            // Fase 2: botón "Ingresar" de 72dp, esquinas 18dp y texto 24sp
            Spacer(Modifier.height(34.dp))
            BotonPrincipal(
                "Ingresar",
                alto = 72.dp,
                radio = 18.dp,
                tamanoTexto = 24.sp,
                onClick = {
                    intentoIngresar = true
                    // Fase 2: solo consulta el Repositorio si los datos son válidos;
                    // si no coinciden con ningún usuario muestra "Teléfono o contraseña incorrectos"
                    if (reglaTelefono == null && reglaContrasena == null) {
                        if (Repositorio.iniciarSesion(telefono, contrasena)) sesionIniciada = true
                        else credencialesIncorrectas = true
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
