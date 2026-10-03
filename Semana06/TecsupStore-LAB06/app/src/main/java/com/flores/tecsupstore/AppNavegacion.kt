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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf("Mis pedidos") }

    var favoritosSet by remember { mutableStateOf(setOf<String>()) }

    val productos = listOf(
        "Audífonos" to "89.00",
        "Smartwatch" to "199.00",
        "Funda celular" to "25.00"
    )

    val colorMoradoBarra = Color(0xFF5B1DA3)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ContenidoDrawer(
                opcionSeleccionada = pantallaActual,
                cantidadFavoritos = favoritosSet.size,
                nombreUsuario = "Yoselin Fabiola Flores Quispe",
                emailUsuario = "yoselin.flores@tecsup.edu.pe",
                onOpcionSeleccionada = { nuevaPantalla ->
                    pantallaActual = nuevaPantalla
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    itemsIndexed(productos) { index, (nombre, precio) ->
                        val esFavorito = favoritosSet.contains(nombre)
                        TarjetaProducto(
                            nombre = nombre,
                            precio = precio,
                            esFavorito = esFavorito,
                            onToggleFavorito = {
                                favoritosSet = if (esFavorito) {
                                    favoritosSet - nombre
                                } else {
                                    favoritosSet + nombre
                                }
                            },
                            // El primer producto ("Audífonos") se muestra con borde morado.
                            // El menú NO se abre por defecto: abrir un DropdownMenu al iniciar
                            // la app crea un popup invisible que bloquea el primer toque.
                            tieneBorde = (index == 0)
                        )
                    }
                }
            }
        }
    }
}