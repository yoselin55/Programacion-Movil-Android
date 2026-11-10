package com.flores.mycar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.flores.mycar.data.perfil.FotoPerfil
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion

/** Una opcion del menu lateral. */
data class OpcionMenu(val ruta: String, val titulo: String, val icono: ImageVector, val cantidad: Int = 0)

/** Contenido del menu lateral: la persona, sus opciones y Cerrar sesion. */
@Composable
fun ContenidoMenu(
    sesion: Sesion,
    nombre: String,
    foto: FotoPerfil?,
    rutaActual: String?,
    carrito: Int,
    rutas: RutasMenu,
    onIr: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Lo que ya esta en la barra de abajo (Inicio, Autos, Perfil/Panel) no se repite aqui.
    val opciones = buildList {
        add(OpcionMenu(rutas.marcas, "Marcas", Icons.Filled.Category))
        if (sesion.rol == Rol.ADMIN) {
            add(OpcionMenu(rutas.usuarios, "Usuarios", Icons.Filled.Groups))
        } else {
            add(OpcionMenu(rutas.favoritos, "Favoritos", Icons.Filled.Star))
            add(OpcionMenu(rutas.carrito, "Carrito de compras", Icons.Filled.ShoppingCart, carrito))
            add(OpcionMenu(rutas.pedidos, "Mis pedidos", Icons.Filled.ShoppingBag))
        }
        add(OpcionMenu(rutas.perfil, "Mi perfil", Icons.Filled.Person))
    }

    Column(Modifier.fillMaxHeight()) {
        // Cabecera con la persona
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 20.dp)
        ) {
            Avatar(nombre = nombre.ifBlank { sesion.correo }, foto = foto, tamano = 64)
            Text(
                nombre.ifBlank { "Mi cuenta" },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                sesion.correo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    if (sesion.rol == Rol.ADMIN) "Administrador" else "Cliente",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp).weight(1f)) {
            opciones.forEach { opcion ->
                NavigationDrawerItem(
                    label = { Text(opcion.titulo) },
                    selected = rutaActual == opcion.ruta,
                    onClick = { onIr(opcion.ruta) },
                    icon = { Icon(opcion.icono, contentDescription = null) },
                    badge = { if (opcion.cantidad > 0) Badge { Text(opcion.cantidad.toString()) } },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = onCerrarSesion,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            modifier = Modifier.padding(12.dp)
        )
    }
}

/** Rutas de las opciones del menu. */
data class RutasMenu(
    val inicio: String,
    val autos: String,
    val marcas: String,
    val favoritos: String,
    val carrito: String,
    val pedidos: String,
    val admin: String,
    val perfil: String,
    val usuarios: String
)

/** Foto de perfil redonda; si no hay, muestra la inicial del nombre. */
@Composable
fun Avatar(nombre: String, foto: FotoPerfil?, tamano: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(tamano.dp)
    ) {
        if (foto != null) {
            AsyncImage(
                model = "file://${foto.archivo.absolutePath}?v=${foto.version}",
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    nombre.firstOrNull { it.isLetterOrDigit() }?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
