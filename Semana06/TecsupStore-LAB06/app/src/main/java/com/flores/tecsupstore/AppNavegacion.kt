package com.flores.tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    var pantallaActual by remember { mutableStateOf("Inicio") }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var sesionActiva by remember { mutableStateOf(true) }

    var favoritosSet by remember { mutableStateOf(setOf<String>()) }

    val nombreUsuario = "Yoselin Fabiola Flores Quispe"
    val emailUsuario = "yoselin.flores@tecsup.edu.pe"

    val productos = listOf(
        "Audífonos" to "89.00",
        "Smartwatch" to "199.00",
        "Funda celular" to "25.00"
    )

    val pedidos = listOf(
        Pedido("#TS-1024", "Smartwatch", "199.00", "En camino"),
        Pedido("#TS-1019", "Audífonos", "89.00", "Entregado"),
        Pedido("#TS-1007", "Funda celular", "25.00", "Entregado")
    )

    val alternarFavorito: (String) -> Unit = { nombre ->
        favoritosSet = if (favoritosSet.contains(nombre)) {
            favoritosSet - nombre
        } else {
            favoritosSet + nombre
        }
    }

    val colorMoradoBarra = Color(0xFF5B1DA3)

    if (!sesionActiva) {
        PantallaSesionCerrada(
            onIniciarSesion = {
                sesionActiva = true
                pantallaActual = "Inicio"
            }
        )
        return
    }

    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoCerrarSesion = false
                    favoritosSet = emptySet()
                    sesionActiva = false
                }) {
                    Text("Cerrar sesión", color = colorMoradoBarra)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val subtitulo = when (pantallaActual) {
        "Mis pedidos" -> "Mis pedidos"
        "Favoritos" -> "Mis favoritos"
        "Perfil" -> "Mi perfil"
        else -> "Más vendidos"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ContenidoDrawer(
                opcionSeleccionada = pantallaActual,
                cantidadFavoritos = favoritosSet.size,
                nombreUsuario = nombreUsuario,
                emailUsuario = emailUsuario,
                onOpcionSeleccionada = { opcion ->
                    // "Cerrar sesión" no es una pantalla: pide confirmación
                    if (opcion == "Cerrar sesión") {
                        mostrarDialogoCerrarSesion = true
                    } else {
                        pantallaActual = opcion
                    }
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
                                text = subtitulo,
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
                when (pantallaActual) {
                    "Mis pedidos" -> PantallaPedidos(pedidos = pedidos)
                    "Favoritos" -> PantallaFavoritos(
                        productos = productos,
                        favoritos = favoritosSet,
                        onToggleFavorito = alternarFavorito
                    )
                    "Perfil" -> PantallaPerfil(
                        nombreUsuario = nombreUsuario,
                        emailUsuario = emailUsuario,
                        cantidadFavoritos = favoritosSet.size,
                        cantidadPedidos = pedidos.size
                    )
                    // El menú de las tarjetas NO se abre por defecto: abrir un DropdownMenu
                    // al iniciar la app crea un popup invisible que bloquea el primer toque.
                    else -> ListaProductos(
                        productos = productos,
                        favoritos = favoritosSet,
                        onToggleFavorito = alternarFavorito
                    )
                }
            }
        }
    }
}
