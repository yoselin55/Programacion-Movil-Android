package com.flores.saludplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flores.saludplus.data.model.Cita
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.data.model.Medico
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.Estrella
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible
import com.flores.saludplus.util.Fechas

// Relaciones:
// - Los usan las pantallas de ui/screens (Splash, Registro, Login, Home y las 3 de la barra inferior)
// - TarjetaCita usa util/Fechas.kt (Fase 2) para mostrar la fecha en español
// Botón azul de ancho completo (Comenzar, Registrarme, Ingresar...)
@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(52.dp)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}

// Campo de texto con ícono, etiqueta y mensaje de error (nombre, teléfono, correo, contraseña)
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = AzulPrimario) },
        isError = error != null,
        supportingText = error?.let { mensaje -> { Text(mensaje) } },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        visualTransformation = if (esContrasena) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth()
    )
}

// Barra superior con título centrado y flecha para volver (pantallas internas)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onBack: () -> Unit, acciones: @Composable RowScope.() -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

// Logo de la clínica: ícono médico sobre un cuadro azul claro
@Composable
fun LogoClinica(tamano: Int = 96) {
    Box(
        modifier = Modifier.size(tamano.dp).background(AzulClaro, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.MedicalServices,
            contentDescription = "Logo SaludPlus",
            tint = AzulPrimario,
            modifier = Modifier.size((tamano * 0.6).dp)
        )
    }
}

// Commit 9: tarjeta de una cita (médico, especialidad, fecha y hora)
@Composable
fun TarjetaCita(cita: Cita, medico: String, especialidad: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(medico, fontWeight = FontWeight.Bold)
            Text(especialidad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Event, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                // Fase 2: fecha corta en español, por ejemplo "Mar 6 oct 2026"
                Text(Fechas.textoCortoDesdeIso(cita.fecha), style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                Text(cita.hora, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Commit 8: fila con ícono, título y valor (datos de la cita)
@Composable
fun FilaDetalle(icono: ImageVector, titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(AzulClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AzulPrimario)
        }
        Column {
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Commit 7: botón seleccionable para días y horas; se pinta de azul al elegirlo
@Composable
fun ChipSeleccion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitulo: String? = null
) {
    val colorTexto = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier
            .height(56.dp)
            .background(if (seleccionado) AzulPrimario else AzulClaro, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(texto, color = colorTexto, style = MaterialTheme.typography.labelMedium)
        if (subtitulo != null) {
            Text(subtitulo, color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}

// Commit 6: campo de búsqueda en tiempo real (Especialidades y Médicos)
@Composable
fun CampoBusqueda(valor: String, onCambio: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}

// Commit 6: fila de una especialidad (ícono, nombre, descripción y flecha)
@Composable
fun ItemEspecialidad(especialidad: Especialidad, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(especialidad.icono, contentDescription = null, tint = AzulPrimario)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(especialidad.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    especialidad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

// Commit 6: tarjeta de un médico (nombre, especialidad, calificación y disponibilidad)
@Composable
fun TarjetaMedico(medico: Medico, especialidad: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(34.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(medico.nombre, fontWeight = FontWeight.Bold)
                Text(especialidad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Estrella, modifier = Modifier.size(16.dp))
                    Text(" ${medico.calificacion} (${medico.resenas})", style = MaterialTheme.typography.bodySmall)
                }
                Box(modifier = Modifier.background(VerdeClaro, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(medico.disponibilidad, style = MaterialTheme.typography.labelSmall, color = VerdeDisponible)
                }
            }
        }
    }
}

// Commit 5: barra inferior con los 4 destinos principales; resalta la ruta actual
@Composable
fun BarraInferior(rutaActual: String, onNavegar: (String) -> Unit) {
    val destinos = listOf(
        Triple("Inicio", Icons.Filled.Home, Rutas.HOME),
        Triple("Citas", Icons.Filled.Event, Rutas.MIS_CITAS),
        Triple("Resultados", Icons.Filled.Description, Rutas.RESULTADOS),
        Triple("Perfil", Icons.Filled.Person, Rutas.PERFIL)
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        destinos.forEach { (etiqueta, icono, ruta) ->
            NavigationBarItem(
                selected = ruta == rutaActual,
                onClick = { if (ruta != rutaActual) onNavegar(ruta) },
                icon = { Icon(icono, contentDescription = etiqueta) },
                label = { Text(etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulPrimario,
                    selectedTextColor = AzulPrimario,
                    indicatorColor = AzulClaro
                )
            )
        }
    }
}

// Commit 4: tarjeta cuadrada de color con ícono y texto (accesos de Inicio)
@Composable
fun TarjetaAccion(
    texto: String,
    icono: ImageVector,
    colorFondo: Color,
    colorIcono: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(110.dp).clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(36.dp))
            Text(texto, color = colorIcono, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

// Contenido temporal de cada pantalla pendiente, con botones para seguir el flujo.
// TODO: quitar su uso en cada pantalla al terminarla
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    acciones: List<Pair<String, () -> Unit>> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Pantalla en construcción", style = MaterialTheme.typography.bodyMedium)
        acciones.forEach { (texto, accion) ->
            Button(onClick = accion) { Text(texto) }
        }
    }
}
