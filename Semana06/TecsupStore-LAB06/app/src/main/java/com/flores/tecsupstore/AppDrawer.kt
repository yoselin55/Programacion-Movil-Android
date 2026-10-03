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

// ====================================================
// HITO 4 - Commit b566a96:
// "crea estructura inicial de NavigationDrawer con encabezado de usuario"
// Encabezado del menú lateral: avatar con iniciales, nombre y correo
// ====================================================
@Composable
fun EncabezadoDrawer() {
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
                text = "MR",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5B1DA3),
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "María Rojas",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF1D1B20)
            )
            Text(
                text = "maria@tecsup.edu.pe",
                fontSize = 13.sp,
                color = Color(0xFF79747E)
            )
        }
    }
}

// ====================================================
// HITO 6 - Commit e293a85:
// "finaliza personalizacion del NavigationDrawer e item activo resaltado"
// Contenido del drawer: encabezado + opciones del menú.
// La opción activa se resalta con color morado y texto en negrita
// ====================================================
@Composable
fun ContenidoDrawer(
    opcionSeleccionada: String,
    onOpcionSeleccionada: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White
    ) {
        EncabezadoDrawer()
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
            val esSeleccionado = (opcionSeleccionada == titulo) // Hito 6: detecta el ítem activo
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
                // Hito 6: colores del ítem activo resaltado
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