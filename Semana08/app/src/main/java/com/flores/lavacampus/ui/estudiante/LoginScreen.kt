package com.flores.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.components.FotoConDegradado
import com.flores.lavacampus.ui.components.LavaButton
import com.flores.lavacampus.ui.components.LavaTextField
import com.flores.lavacampus.ui.components.LogoLavaCampus
import com.flores.lavacampus.ui.components.SelectorPestanas
import com.flores.lavacampus.ui.theme.LavaTheme
import com.flores.lavacampus.viewmodel.SesionViewModel

// Un solo Login para ambos roles: el selector cambia el color de la pantalla.
// Estudiante: puede iniciar sesion o registrarse. Administrador: solo inicia sesion
@Composable
fun LoginScreen(vm: SesionViewModel, onLoginExitoso: (Rol) -> Unit) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var registrando by remember { mutableStateOf(false) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    // Campos solo del registro
    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }

    val error by vm.error.collectAsState()
    val errores by vm.erroresRegistro.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    // El administrador no se registra: si cambia de pestana se vuelve al inicio de sesion
    val modoRegistro = registrando && rol == Rol.CLIENTE

    LavaTheme(rol = rol, darkTheme = oscuro) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.imePadding().verticalScroll(rememberScrollState())) {
                // Banner con foto de la lavanderia y el logo (el color cambia segun el perfil elegido)
                FotoConDegradado(
                    R.drawable.foto_lavanderia,
                    Modifier.fillMaxWidth().height(if (modoRegistro) 200.dp else 270.dp),
                    intensidad = 0.9f
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LogoLavaCampus(if (modoRegistro) 80.dp else 112.dp)
                        Spacer(Modifier.height(10.dp))
                        Text("LavaCampus", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tu lavandería en un solo lugar", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        if (modoRegistro) "Crea tu cuenta" else "Te damos la bienvenida",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        when {
                            modoRegistro -> "Solo para estudiantes. Completa tus datos"
                            rol == Rol.ADMIN -> "Acceso solo para el personal de la lavandería"
                            else -> "Inicia sesión para continuar"
                        },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    SelectorRol(rol) {
                        rol = it
                        vm.limpiarError(); vm.limpiarErroresRegistro()
                    }

                    // Solo el estudiante elige entre iniciar sesion y registrarse
                    if (rol == Rol.CLIENTE) {
                        SelectorPestanas(
                            opciones = listOf("Iniciar sesión", "Registrarse"),
                            seleccionada = if (registrando) 1 else 0,
                            onElegir = { registrando = it == 1; vm.limpiarError(); vm.limpiarErroresRegistro() }
                        )
                    }

                    if (modoRegistro) {
                        LavaTextField(
                            nombre, { nombre = it; vm.limpiarErrorCampo("nombre") }, "Nombre completo", Modifier.fillMaxWidth(),
                            icono = Icons.Outlined.Person, error = errores["nombre"]
                        )
                        LavaTextField(
                            codigo, { codigo = it; vm.limpiarErrorCampo("codigo") }, "Código de estudiante", Modifier.fillMaxWidth(),
                            icono = Icons.Outlined.Badge, mayusculas = true, error = errores["codigo"]
                        )
                    }

                    LavaTextField(
                        value = correo,
                        onValueChange = { correo = it; vm.limpiarErrorCampo("correo") },
                        etiqueta = "Correo",
                        icono = Icons.Outlined.Email,
                        teclado = KeyboardType.Email,
                        error = if (modoRegistro) errores["correo"] else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LavaTextField(
                        value = clave,
                        onValueChange = { clave = it; vm.limpiarErrorCampo("clave") },
                        etiqueta = "Contraseña",
                        icono = Icons.Outlined.Lock,
                        esClave = true,
                        error = if (modoRegistro) errores["clave"] else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (modoRegistro) {
                        LavaTextField(
                            confirmacion, { confirmacion = it; vm.limpiarErrorCampo("confirmacion") }, "Repite la contraseña", Modifier.fillMaxWidth(),
                            icono = Icons.Outlined.Lock, esClave = true, error = errores["confirmacion"]
                        )
                    }

                    if (!modoRegistro) {
                        error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold) }
                    }

                    LavaButton(
                        texto = if (modoRegistro) "Crear cuenta" else "Ingresar",
                        onClick = {
                            if (modoRegistro) vm.registrar(nombre, codigo, correo, clave, confirmacion, onLoginExitoso)
                            else vm.login(correo, clave, rol, onLoginExitoso)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Enlace para cambiar de modo (solo estudiantes)
                    if (rol == Rol.CLIENTE) {
                        Text(
                            if (modoRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { registrando = !registrando; vm.limpiarError(); vm.limpiarErroresRegistro() }
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// Selector en forma de pastilla: la opcion elegida se pinta con el color del perfil
@Composable
private fun SelectorRol(seleccionado: Rol, onElegir: (Rol) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
            .padding(4.dp)
    ) {
        Rol.entries.forEach { opcion ->
            val activo = opcion == seleccionado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (activo) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onElegir(opcion) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    opcion.texto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
