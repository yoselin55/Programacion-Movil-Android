package com.flores.campusgps.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flores.campusgps.R
import com.flores.campusgps.ui.theme.AzulClaro
import com.flores.campusgps.ui.theme.Fondo
import com.flores.campusgps.ui.theme.Tinta
import com.flores.campusgps.ui.theme.TintaSuave
import com.flores.campusgps.viewmodel.BienvenidaViewModel
import com.flores.campusgps.viewmodel.MapaViewModel
import kotlinx.coroutines.launch

// Mejora 3 (menú lateral): las secciones de la app.
enum class Seccion(val titulo: String, val icono: ImageVector) {
    MAPA("Mapa", Icons.Filled.Map),
    RECIENTES("Recientes", Icons.Filled.History),
    PERFIL("Mi perfil", Icons.Filled.Person)
}

// Mejora 3: contenedor de la app con el menú lateral y la barra superior; cambia entre las secciones.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(vm: MapaViewModel, perfilVm: BienvenidaViewModel) {
    val nombre by perfilVm.nombre.collectAsState()
    var seccion by rememberSaveable { mutableStateOf(Seccion.MAPA) }
    val estadoMenu = rememberDrawerState(DrawerValue.Closed)
    val corrutina = rememberCoroutineScope()

    // El botón Atrás vuelve al mapa si estás en otra sección.
    BackHandler(enabled = seccion != Seccion.MAPA && !estadoMenu.isOpen) { seccion = Seccion.MAPA }

    ModalNavigationDrawer(
        drawerState = estadoMenu,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Fondo) {
                // Cabecera del menú: logo, nombre de la app y saludo.
                Column(modifier = Modifier.padding(24.dp)) {
                    Image(
                        painter = painterResource(R.drawable.logo_campusgps),
                        contentDescription = "Logo de CampusGPS",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("CampusGPS", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = if (!nombre.isNullOrBlank()) "Hola, $nombre" else "Bienvenido",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = AzulClaro)
                Spacer(Modifier.height(12.dp))

                Seccion.entries.forEach { destino ->
                    NavigationDrawerItem(
                        label = { Text(destino.titulo) },
                        icon = { Icon(destino.icono, contentDescription = null) },
                        selected = seccion == destino,
                        onClick = {
                            seccion = destino
                            corrutina.launch { estadoMenu.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = AzulClaro,
                            selectedTextColor = Tinta,
                            selectedIconColor = Tinta,
                            unselectedTextColor = Tinta,
                            unselectedIconColor = TintaSuave
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider(color = AzulClaro)
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = TintaSuave)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Versión 1.0 · Proyecto académico",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Fondo,
            topBar = {
                // Mejora 3: el mapa no lleva barra superior (el buscador flotante ya trae el menú y tu avatar);
                // las demás secciones sí la llevan.
                if (seccion != Seccion.MAPA) TopAppBar(
                    // Mejora 3: la barra lleva el botón del menú, el logo, el título de la sección y un saludo.
                    navigationIcon = {
                        IconButton(onClick = { corrutina.launch { estadoMenu.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.logo_campusgps),
                                contentDescription = "Logo de CampusGPS",
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    if (seccion == Seccion.MAPA) "CampusGPS" else seccion.titulo,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!nombre.isNullOrBlank()) {
                                    Text("Hola, $nombre", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { relleno ->
            val modificador = Modifier.padding(relleno)
            when (seccion) {
                Seccion.MAPA -> PantallaMapa(
                    vm = vm,
                    nombre = nombre ?: "",
                    onAbrirMenu = { corrutina.launch { estadoMenu.open() } },
                    onAbrirPerfil = { seccion = Seccion.PERFIL }
                )

                Seccion.RECIENTES -> PantallaRecientes(
                    vm = vm,
                    // Tocar un lugar o una búsqueda reciente lleva de vuelta al mapa.
                    onVerEnMapa = { id ->
                        vm.seleccionar(id)
                        seccion = Seccion.MAPA
                    },
                    onBuscar = { texto ->
                        vm.cambiarConsulta(texto)
                        seccion = Seccion.MAPA
                    },
                    modifier = modificador
                )

                Seccion.PERFIL -> PantallaPerfil(
                    nombre = nombre ?: "",
                    vm = vm,
                    onGuardarNombre = { perfilVm.guardarNombre(it) },
                    modifier = modificador
                )
            }
        }
    }
}
