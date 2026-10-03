package com.flores.tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ====================================================
// HITO 5 - Commit 86b5cad:
// "integra NavigationDrawer con Scaffold y navegacion entre pantallas"
// ModalNavigationDrawer + Scaffold con barra superior y botón de menú
// ====================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    // Hito 5: estado del drawer, scope para abrir/cerrar y pantalla actual
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf("Mis pedidos") }

    // Hito 6 (Commit e293a85): lista de productos de ejemplo
    val productos = listOf(
        "Audífonos" to "89.00",
        "Smartwatch" to "199.00",
        "Funda celular" to "25.00"
    )

    val colorMoradoBarra = Color(0xFF5B1DA3)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Hito 6 (Commit e293a85): drawer con opciones; al elegir una
            // se cambia la pantalla y se cierra el menú
            ContenidoDrawer(
                opcionSeleccionada = pantallaActual,
                onOpcionSeleccionada = { nuevaPantalla ->
                    pantallaActual = nuevaPantalla
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        // Hito 5 (Commit 86b5cad): Scaffold con barra superior; el ícono Menu abre el drawer
        Scaffold(
            topBar = {
                Surface(
                    color = colorMoradoBarra,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "TECSUP Store",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Más vendidos",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                // Hito 6 (Commit e293a85): lista de TarjetaProducto (Hitos 1-3)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    itemsIndexed(productos) { index, (nombre, precio) ->
                        TarjetaProducto(
                            nombre = nombre,
                            precio = precio,
                            abiertoPorDefecto = (index == 0), // Abre el menú de Audífonos por defecto
                            tieneBorde = (index == 0)        // Aplica el borde morado al primer producto
                        )
                    }
                }
            }
        }
    }
}