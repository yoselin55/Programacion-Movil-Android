package com.flores.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.data.prefs.FotoPerfil
import com.flores.lavacampus.model.EstadoPedido

// Ancho fijo del menu: si cambiara cuando carga el usuario, el menu quedaria abierto sin que nadie lo abra
val ANCHO_MENU = 310.dp

// Menu sin contenido, para antes de iniciar sesion (mantiene el mismo ancho)
@Composable
fun MenuLateralVacio() {
    ModalDrawerSheet(modifier = Modifier.width(ANCHO_MENU), drawerContainerColor = Color.Transparent) {}
}

// Un destino del menu lateral: su nombre, su icono, la ruta a la que lleva y, si hay, cuantos avisos tiene
class ItemMenu(val texto: String, val icono: ImageVector, val ruta: String, val avisos: Int = 0)

// Boton de tres rayas que abre el menu lateral
@Composable
fun BotonMenu(onClick: () -> Unit, modifier: Modifier = Modifier, sobreFoto: Boolean = false) {
    Box(
        modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (sobreFoto) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Rounded.Menu,
            contentDescription = "Abrir el menú",
            tint = if (sobreFoto) Color.White else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Menu lateral de cristal: el panel es un poco transparente y deja ver la pantalla de atras.
// Arriba el usuario, en medio sus destinos (las notificaciones con su contador) y abajo cerrar sesion
@Composable
fun MenuLateral(
    nombre: String,
    correo: String,
    etiqueta: String,
    foto: FotoPerfil?,
    items: List<ItemMenu>,
    rutaActual: String?,
    onElegir: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(ANCHO_MENU),
        drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(Modifier.fillMaxHeight().padding(horizontal = 18.dp, vertical = 22.dp)) {
            // Usuario
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AvatarUsuario(nombre, foto, 60.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(correo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    Text(
                        etiqueta,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            // Destinos
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEach { item -> FilaMenu(item, item.ruta == rutaActual) { onElegir(item.ruta) } }
            }

            Spacer(Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(10.dp))
            // Cerrar sesion
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onCerrarSesion() }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = EstadoPedido.CANCELADO.color)
                Text("Cerrar sesión", fontWeight = FontWeight.SemiBold, color = EstadoPedido.CANCELADO.color)
            }
        }
    }
}

@Composable
private fun FilaMenu(item: ItemMenu, activa: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (activa) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            item.icono,
            contentDescription = null,
            tint = if (activa) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
        Text(
            item.texto,
            modifier = Modifier.weight(1f),
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
            color = if (activa) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
        // Globo con los avisos sin leer
        if (item.avisos > 0) {
            Box(
                Modifier
                    .defaultMinSize(minWidth = 22.dp, minHeight = 22.dp)
                    .clip(CircleShape)
                    .background(EstadoPedido.CANCELADO.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (item.avisos > 9) "9+" else item.avisos.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }
    }
}
