package com.flores.lavacampus.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.flores.lavacampus.data.prefs.FotoPerfil
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.flores.lavacampus.R

// Piezas que comparten Perfil (estudiante) y Ajustes (administrador)

@Composable
fun CabeceraPerfil(
    nombre: String,
    correo: String,
    etiqueta: String,
    foto: FotoPerfil? = null,
    onCambiarFoto: (() -> Unit)? = null,
    onQuitarFoto: (() -> Unit)? = null
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Cabecera con la foto de la lavanderia, la foto de perfil al centro y los datos debajo
        FotoConDegradado(
            R.drawable.foto_lavanderia,
            Modifier.fillMaxWidth().height(285.dp).clip(RoundedCornerShape(28.dp)),
            intensidad = 0.9f
        ) {
            Column(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Foto de perfil: al tocarla se abre la galeria
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.size(108.dp).clip(CircleShape).background(Color.White).padding(4.dp)) {
                        AvatarUsuario(
                            nombre, foto, 100.dp,
                            Modifier.then(if (onCambiarFoto != null) Modifier.clickable { onCambiarFoto() } else Modifier)
                        )
                    }
                    if (onCambiarFoto != null) {
                        Box(
                            Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { onCambiarFoto() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt, "Cambiar foto",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.White)
                Text(correo, fontSize = 13.sp, color = Color.White.copy(alpha = 0.88f))
                Spacer(Modifier.height(8.dp))
                Text(
                    etiqueta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }
        if (onCambiarFoto != null) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                AccionFoto(
                    if (foto == null) "Agregar foto" else "Cambiar foto",
                    Icons.Filled.CameraAlt,
                    MaterialTheme.colorScheme.primary,
                    onCambiarFoto
                )
                if (foto != null && onQuitarFoto != null) {
                    AccionFoto("Quitar", Icons.Outlined.Delete, EstadoPedido.CANCELADO.color, onQuitarFoto)
                }
            }
        }
    }
}

// Boton en forma de pildora bajo la cabecera (agregar, cambiar o quitar la foto)
@Composable
private fun AccionFoto(texto: String, icono: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icono, null, tint = color, modifier = Modifier.size(18.dp))
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// Abre la galeria del telefono (selector de fotos del sistema, sin pedir permisos)
@Composable
fun rememberSelectorFoto(onElegida: (Uri) -> Unit): () -> Unit {
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onElegida(uri)
    }
    return { lanzador.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
}

// Cambiar de perfil pide las credenciales del otro perfil: no es un salto libre
@Composable
fun DialogoCambiarPerfil(
    destino: String,
    error: String?,
    onConfirmar: (correo: String, clave: String) -> Unit,
    onCerrar: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Cambiar a $destino", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Por seguridad, inicia sesión con una cuenta de $destino.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                LavaTextField(correo, { correo = it }, "Correo", Modifier.fillMaxWidth(), icono = Icons.Outlined.Email)
                LavaTextField(
                    clave, { clave = it }, "Contraseña", Modifier.fillMaxWidth(),
                    icono = Icons.Outlined.Lock, esClave = true
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirmar(correo, clave) }) { Text("Cambiar", fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

// Tarjeta de color: modo practica para saltar al otro perfil
@Composable
fun BotonCambiarPerfil(subtitulo: String, onClick: () -> Unit) {
    // En modo oscuro el primario es un rosa muy claro: se usa el contenedor, mas sobrio
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val fondo = if (oscuro) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        modifier = Modifier.fillMaxWidth()
    ) {
        val sobre = if (oscuro) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.SwapHoriz, null, tint = sobre, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text("Cambiar de perfil", fontWeight = FontWeight.Bold, color = sobre, fontSize = 14.sp)
                Text(subtitulo, fontSize = 11.sp, color = sobre.copy(alpha = 0.85f))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = sobre)
        }
    }
}

@Composable
fun GrupoPreferencias(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) { Column { contenido() } }
}

@Composable
fun FilaSwitch(titulo: String, detalle: String, activo: Boolean, onCambio: (Boolean) -> Unit, icono: ImageVector? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (icono != null) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Icon(icono, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
        }
        Column(Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(detalle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}

@Composable
fun NotaDataStore() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Text(
            "Tus preferencias se guardan en el dispositivo con DataStore",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun BotonCerrarSesion(onClick: () -> Unit) {
    val rojo = EstadoPedido.CANCELADO.color
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, rojo),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = rojo),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Icon(Icons.AutoMirrored.Outlined.Logout, null, modifier = Modifier.size(20.dp))
        Text("Cerrar sesión", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
    }
}

// Opcion tipo pastilla (Kilo/Prenda/Unidad, 24/48/72 h)
@Composable
fun OpcionPildora(texto: String, seleccionada: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val primario = MaterialTheme.colorScheme.primary
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionada) primario else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (seleccionada) primario else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(vertical = 11.dp)
    ) {
        Text(
            texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}
