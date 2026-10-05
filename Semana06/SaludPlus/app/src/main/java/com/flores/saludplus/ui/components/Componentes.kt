package com.flores.saludplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
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
import com.flores.saludplus.navigation.Rutas
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario

// Relaciones:
// - Los usan las pantallas de ui/screens (Splash, Registro, Login, Home y las 3 de la barra inferior)
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
fun BarraSuperior(titulo: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
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
