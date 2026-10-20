package com.flores.lavacampus.ui.components

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
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.model.EstadoPedido

// Piezas que comparten Perfil (estudiante) y Ajustes (administrador)

@Composable
fun CabeceraPerfil(nombre: String, correo: String, etiqueta: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) { Icon(Icons.Outlined.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp)) }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(correo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(
                    etiqueta,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// Tarjeta de color: modo practica para saltar al otro perfil
@Composable
fun BotonCambiarPerfil(subtitulo: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
    ) {
        val sobre = MaterialTheme.colorScheme.onPrimary
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
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) { Column { contenido() } }
}

@Composable
fun FilaSwitch(titulo: String, detalle: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(detalle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}

@Composable
fun NotaDataStore() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(14.dp)
    ) {
        Text(
            "Se guardan en el dispositivo",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text("con DataStore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
        modifier = Modifier.fillMaxWidth()
    ) { Text("Cerrar sesión", fontWeight = FontWeight.SemiBold) }
}

// Opcion tipo pastilla (Kilo/Prenda/Unidad, 24/48/72 h)
@Composable
fun OpcionPildora(texto: String, seleccionada: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val primario = MaterialTheme.colorScheme.primary
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (seleccionada) primario else Color.White)
            .border(1.dp, if (seleccionada) primario else Color(0xFFE2E8F0), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(vertical = 11.dp)
    ) {
        Text(
            texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (seleccionada) MaterialTheme.colorScheme.onPrimary else Color(0xFF475569)
        )
    }
}
