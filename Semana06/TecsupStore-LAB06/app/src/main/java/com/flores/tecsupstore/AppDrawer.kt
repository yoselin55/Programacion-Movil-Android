package com.flores.tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EncabezadoDrawer(
    nombreUsuario: String = "Yoselin Fabiola Flores Quispe",
    emailUsuario: String = "yoselin.flores@tecsup.edu.pe"
) {
    val iniciales = nombreUsuario
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifEmpty { "YF" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8DEF8)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5B1DA3),
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = nombreUsuario,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1D1B20)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = emailUsuario,
                fontSize = 12.sp,
                color = Color(0xFF79747E)
            )
        }
    }
}

@Composable
fun ContenidoDrawer(
    opcionSeleccionada: String,
    cantidadFavoritos: Int,
    nombreUsuario: String = "Yoselin Fabiola Flores Quispe",
    emailUsuario: String = "yoselin.flores@tecsup.edu.pe",
    onOpcionSeleccionada: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        EncabezadoDrawer(
            nombreUsuario = nombreUsuario,
            emailUsuario = emailUsuario
        )
        HorizontalDivider(color = Color(0xFFE7E0EC))
        Spacer(modifier = Modifier.height(12.dp))

        val opciones = listOf(
            "Inicio",
            "Mis pedidos",
            "Favoritos",
            "Perfil",
            "Cerrar sesión"
        )

        opciones.forEach { titulo ->
            val esSeleccionado = (opcionSeleccionada == titulo)
            NavigationDrawerItem(
                label = {
                    Text(
                        text = titulo,
                        fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                },
                selected = esSeleccionado,
                onClick = { onOpcionSeleccionada(titulo) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = titulo,
                        tint = if (esSeleccionado) Color(0xFF5B1DA3) else Color(0xFF49454F)
                    )
                },
                badge = {
                    if (titulo == "Favoritos" && cantidadFavoritos > 0) {
                        Badge(
                            containerColor = Color(0xFF5B1DA3),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = cantidadFavoritos.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFFF3EDF7),
                    selectedIconColor = Color(0xFF5B1DA3),
                    selectedTextColor = Color(0xFF5B1DA3),
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = Color(0xFF1D1B20)
                ),
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .padding(vertical = 2.dp)
            )
        }
    }
}