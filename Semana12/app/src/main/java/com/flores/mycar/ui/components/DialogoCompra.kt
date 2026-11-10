package com.flores.mycar.ui.components

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de compra (ocupa toda la pantalla, no es una ventana emergente, para que siempre responda).
 * La persona elige: recoger en la agencia o envio a su casa (direccion o ubicacion actual), y el dia.
 * Devuelve (dia, direccion): la direccion viene vacia si va a recoger. Es una compra ficticia: no se pide ningun dato de pago.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoCompra(
    resumen: String,
    total: String,
    onConfirmar: (recojo: String, direccion: String) -> Unit,
    onCancelar: () -> Unit
) {
    BackHandler(onBack = onCancelar)
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val dias = remember { proximosDias(14) }
    var modo by rememberSaveable { mutableIntStateOf(0) }       // 0 = agencia, 1 = domicilio
    var fecha by rememberSaveable { mutableStateOf(dias.first().first) }
    var direccion by rememberSaveable { mutableStateOf("") }
    var buscando by remember { mutableStateOf(false) }

    suspend fun llenarConUbicacion() {
        buscando = true
        val actual = direccionActual(contexto)
        buscando = false
        if (actual != null) direccion = actual
        else Toast.makeText(contexto, "No se pudo obtener tu ubicación. Escribe tu dirección.", Toast.LENGTH_LONG).show()
    }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (permisos.values.any { it }) alcance.launch { llenarConUbicacion() }
    }

    val listo = fecha.isNotBlank() && (modo == 0 || direccion.isNotBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar compra") },
                navigationIcon = { IconoAtras(onCancelar) }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Button(
                    enabled = listo,
                    onClick = { onConfirmar(fecha, if (modo == 0) "" else direccion.trim()) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(56.dp)
                ) {
                    Icon(Icons.Filled.Payments, contentDescription = null)
                    Text("Comprar · $total", modifier = Modifier.padding(start = 10.dp))
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Tu compra", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(resumen, style = MaterialTheme.typography.titleLarge)
                    Text(total, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Text("¿Cómo quieres tu auto?", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
            Opcion("Recoger en la agencia", "Tú vienes a buscarlo", Icons.Filled.Storefront, modo == 0) { modo = 0 }
            Opcion("Envío a mi casa", "Te lo llevamos", Icons.Filled.LocalShipping, modo == 1) { modo = 1 }

            AnimatedVisibility(visible = modo == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección de tu casa") },
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        enabled = !buscando,
                        onClick = {
                            if (tienePermisoDeUbicacion(contexto)) alcance.launch { llenarConUbicacion() }
                            else pedirPermiso.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = null)
                        Text(if (buscando) "Buscando..." else "Usar mi ubicación actual", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    if (modo == 0) "¿Qué día vienes a recogerlo?" else "¿Qué día quieres que te lo envíen?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dias.forEach { (valor, etiqueta) ->
                    FilterChip(selected = fecha == valor, onClick = { fecha = valor }, label = { Text(etiqueta) })
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Opcion(titulo: String, detalle: String, icono: ImageVector, elegida: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (elegida) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(if (elegida) 2.dp else 1.dp, if (elegida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Column(Modifier.padding(start = 14.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
