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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.ui.components.LavaButton
import com.flores.lavacampus.ui.components.LavaTextField
import com.flores.lavacampus.ui.theme.LavaTheme
import com.flores.lavacampus.viewmodel.SesionViewModel

// Un solo Login para ambos roles: el selector cambia el color de la pantalla
@Composable
fun LoginScreen(vm: SesionViewModel, onLoginExitoso: (Rol) -> Unit) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    val error by vm.error.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    LavaTheme(rol = rol, darkTheme = oscuro) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                Text(
                    "Bienvenido",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Ingresa para continuar",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))

                SelectorRol(rol) { rol = it; vm.limpiarError() }

                LavaTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    etiqueta = "Correo",
                    icono = Icons.Outlined.Email,
                    modifier = Modifier.fillMaxWidth()
                )
                LavaTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    etiqueta = "Contraseña",
                    icono = Icons.Outlined.Lock,
                    esClave = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                LavaButton(
                    texto = "Ingresar",
                    onClick = { vm.login(correo, clave, rol, onLoginExitoso) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (rol == Rol.CLIENTE) {
                    Text(
                        "¿No tienes cuenta?  Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Aviso del modo practica (ambos perfiles conviven en la app)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(16.dp)
                    ) {
                        Text(
                            "Modo práctica",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            .background(Color(0xFFF1F5F9))
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) MaterialTheme.colorScheme.onPrimary else Color(0xFF64748B)
                )
            }
        }
    }
}
