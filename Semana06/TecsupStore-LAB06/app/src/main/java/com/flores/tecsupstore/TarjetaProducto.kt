package com.flores.tecsupstore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
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
    esFavorito: Boolean,
    onToggleFavorito: () -> Unit,
    abiertoPorDefecto: Boolean = false,
    tieneBorde: Boolean = false
) {
    // Estado para controlar la apertura/cierre del menú de 3 puntos
    var menuExpandido by remember { mutableStateOf(abiertoPorDefecto) }

    val colorMorado = Color(0xFF5B1DA3)
    val colorFondoTarjeta = if (tieneBorde) Color.White else Color(0xFFF5F0FB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoTarjeta),
        border = if (tieneBorde) BorderStroke(1.5.dp, colorMorado) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de bolsa morada dentro de contenedor redondeado
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE8DEF8), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Producto",
                    tint = colorMorado,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre y Precio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/ $precio",
                    fontSize = 14.sp,
                    color = Color(0xFF49454F)
                )
            }

            // BOTÓN DE 3 PUNTOS (⋮) Y DROPDOWNMENU CONTEXTUAL
            Box {
                IconButton(onClick = { menuExpandido = !menuExpandido }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color(0xFF1D1B20)
                    )
                }

                // Menú desplegable con diseño calcado a la imagen de la guía
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    // 1. Opcion Favoritos
                    DropdownMenuItem(
                        text = { Text("Favoritos", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoritos",
                                tint = if (esFavorito) colorMorado else Color(0xFF49454F)
                            )
                        },
                        onClick = {
                            onToggleFavorito()
                            menuExpandido = false
                        }
                    )

                    HorizontalDivider(color = Color(0xFFE7E0EC))

                    // 2. Opción Compartir
                    DropdownMenuItem(
                        text = { Text("Compartir", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = Color(0xFF49454F)
                            )
                        },
                        onClick = { menuExpandido = false }
                    )

                    HorizontalDivider(color = Color(0xFFE7E0EC))

                    // 3. Opción Reportar
                    DropdownMenuItem(
                        text = { Text("Reportar", fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = "Reportar",
                                tint = Color(0xFF49454F)
                            )
                        },
                        onClick = { menuExpandido = false }
                    )
                }
            }
        }
    }
}