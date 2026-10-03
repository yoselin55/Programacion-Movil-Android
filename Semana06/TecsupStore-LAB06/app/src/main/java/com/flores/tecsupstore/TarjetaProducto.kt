package com.flores.tecsupstore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TarjetaProducto(
    nombre: String,
    precio: String,
    abiertoPorDefecto: Boolean = false,
    tieneBorde: Boolean = false
) {
    // ====================================================
    // HITO 1 - Commit 49577ba:
    // "agrega icono de 3 puntos y estado expanded en TarjetaProducto"
    // Estado que controla si el menú está abierto o cerrado
    // ====================================================
    var expanded by remember { mutableStateOf(abiertoPorDefecto) }

    val colorMoradoPrincipal = Color(0xFF5B1DA3)
    val colorFondoTarjeta = Color(0xFFF4EFFA)
    val colorFondoIcono = Color(0xFFE8DEF8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoTarjeta),
        border = if (tieneBorde) BorderStroke(2.dp, colorMoradoPrincipal) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de Bolsa
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colorFondoIcono, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = colorMoradoPrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "S/ $precio",
                    fontSize = 14.sp,
                    color = Color(0xFF49454F)
                )
            }

            // ====================================================
            // HITO 1 - Commit 49577ba:
            // "agrega icono de 3 puntos y estado expanded en TarjetaProducto"
            // Botón con ícono MoreVert (3 puntos) que abre el menú
            // ====================================================
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color(0xFF1D1B20)
                    )
                }

                // ====================================================
                // HITO 2 - Commit 4ca9a2f:
                // "implementa DropdownMenu contextual basico en tarjeta de producto"
                // DropdownMenu con las opciones Favoritos, Compartir y Reportar
                //
                // HITO 3 - Commit c319c83:
                // "personaliza DropdownMenu con iconos y divisores"
                // Se agregan leadingIcon a cada opción y HorizontalDivider entre ellas
                // ====================================================
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Favoritos", color = Color(0xFF1D1B20)) },
                        leadingIcon = { // Hito 3: ícono
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF1D1B20))
                        },
                        onClick = { expanded = false }
                    )
                    HorizontalDivider(color = Color(0xFFE7E0EC)) // Hito 3: divisor
                    DropdownMenuItem(
                        text = { Text("Compartir", color = Color(0xFF1D1B20)) },
                        leadingIcon = { // Hito 3: ícono
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF1D1B20))
                        },
                        onClick = { expanded = false }
                    )
                    HorizontalDivider(color = Color(0xFFE7E0EC)) // Hito 3: divisor
                    DropdownMenuItem(
                        text = { Text("Reportar", color = Color(0xFF1D1B20)) },
                        leadingIcon = { // Hito 3: ícono
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF1D1B20))
                        },
                        onClick = { expanded = false }
                    )
                }
            }
        }
    }
}