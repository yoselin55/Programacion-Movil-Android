package com.flores.saludplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.saludplus.data.repository.Repositorio
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulOscuro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.Divisor
import com.flores.saludplus.ui.theme.GrisMarcado
import com.flores.saludplus.ui.theme.RojoAviso
import com.flores.saludplus.ui.theme.TextoPrincipal
import com.flores.saludplus.ui.theme.TextoSecundario
import com.flores.saludplus.util.iniciales

// Relaciones:
// - Lo dibuja AppNavigation dentro de un único ModalNavigationDrawer que envuelve todo el NavHost
// - Lo abren HomeScreen, SedesScreen, DoctoresScreen y MisCitasScreen con el botón de BotonMenu
// - Navega con onNavegar (Rutas.SEDES, DOCTORES y MIS_CITAS) y cierra la sesión con Repositorio.cerrarSesion
// - Usa iniciales (util/Textos.kt) para el avatar del encabezado

// Fase 3: menú lateral de la app (320dp, fondo blanco) con solo 4 opciones: Sedes, Doctores, Agenda y
// Cerrar sesión. Lo que ya está aquí no se repite en el Inicio. rutaActual marca la opción activa.
@Composable
fun MenuLateral(
    rutaActual: String?,
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = Repositorio.usuarioActual
    // Pide confirmación antes de cerrar la sesión
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(320.dp)
    ) {
        // Encabezado de 170dp con fondo azul claro: avatar de 72dp, nombre y teléfono
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(AzulClaro)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(72.dp).background(AzulPrimario, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales(usuario?.nombre ?: ""), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                usuario?.nombre ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(usuario?.telefono ?: "", fontSize = 16.sp, color = TextoSecundario)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ItemMenu("Sedes", Icons.Filled.LocationOn, activo = esDeSedes(rutaActual)) { onNavegar(Rutas.SEDES) }
            ItemMenu("Doctores", Icons.Filled.MedicalServices, activo = esDeDoctores(rutaActual)) { onNavegar(Rutas.DOCTORES) }
            ItemMenu("Agenda", Icons.Filled.EventAvailable, activo = rutaActual == Rutas.MIS_CITAS) { onNavegar(Rutas.MIS_CITAS) }
            HorizontalDivider(
                thickness = 1.dp,
                color = Divisor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
            ItemMenu("Cerrar sesión", Icons.AutoMirrored.Filled.Logout, activo = false, peligro = true) {
                confirmarSalida = true
            }
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("¿Cerrar sesión?", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Tendrás que ingresar de nuevo con tu teléfono y contraseña.", fontSize = 18.sp) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    Repositorio.cerrarSesion()
                    onCerrarSesion()
                }) { Text("Cerrar sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = RojoAviso) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar", fontSize = 18.sp) }
            }
        )
    }
}

// Fase 3: las pantallas del flujo de agendamiento (sede, especialidad, médicos, fecha, confirmar...)
// cuentan como "Sedes" en el menú
private fun esDeSedes(ruta: String?): Boolean =
    ruta != null && listOf(
        Rutas.SEDES, Rutas.SEDE_DETALLE, Rutas.ESPECIALIDADES, Rutas.MEDICOS,
        Rutas.FECHA_HORA, Rutas.CONFIRMAR, Rutas.CITA_EXITOSA
    ).contains(ruta)

// Fase 3: las pantallas del directorio de doctores cuentan como "Doctores"
private fun esDeDoctores(ruta: String?): Boolean =
    ruta == Rutas.DOCTORES || ruta == Rutas.DOCTORES_ESPECIALIDAD

// Fase 3: ítem del menú de 56dp con ícono de 28dp y texto de 20sp; el activo va con fondo azul claro,
// texto azul y negrita; los demás en gris oscuro (o rojo si es "peligro", como Cerrar sesión)
@Composable
private fun ItemMenu(
    texto: String,
    icono: ImageVector,
    activo: Boolean,
    peligro: Boolean = false,
    onClick: () -> Unit
) {
    val colorNormal = if (peligro) RojoAviso else GrisMarcado
    NavigationDrawerItem(
        label = { Text(texto, fontSize = 20.sp, fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal) },
        icon = { Icon(icono, contentDescription = null, modifier = Modifier.size(28.dp)) },
        selected = activo,
        onClick = onClick,
        modifier = Modifier.height(56.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AzulClaro,
            unselectedContainerColor = Color.Transparent,
            selectedIconColor = AzulPrimario,
            selectedTextColor = AzulPrimario,
            unselectedIconColor = colorNormal,
            unselectedTextColor = colorNormal
        )
    )
}

// Fase 3: botón de las tres rayas (30dp, zona táctil de 48dp) que abre el menú lateral
@Composable
fun BotonMenu(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
        Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = TextoPrincipal, modifier = Modifier.size(30.dp))
    }
}

// Fase 3: encabezado de las pantallas principales (Agenda, Sedes y Doctores): botón de menú arriba
// y título de 34sp negrita azul marino, igual en todas
@Composable
fun EncabezadoConMenu(titulo: String, onMenu: () -> Unit, subtitulo: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        BotonMenu(onMenu)
        Text(titulo, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        if (subtitulo != null) {
            Text(subtitulo, fontSize = 18.sp, color = TextoSecundario)
        }
        Spacer(Modifier.height(16.dp))
    }
}
