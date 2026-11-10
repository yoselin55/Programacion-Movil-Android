package com.flores.mycar.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.ui.components.BarraPrincipal

/** Panel del administrador: totales, gestion (pedidos, citas, catalogo, usuarios) y datos. Solo lo ve el rol admin. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    correo: String,
    onGestion: () -> Unit,
    onCatalogo: () -> Unit,
    onUsuarios: () -> Unit,
    onPromociones: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { BarraPrincipal("Panel Admin", onMenu = onMenu) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera
            com.flores.mycar.ui.components.CarruselFotos(
                fotos = listOf(
                    com.flores.mycar.R.drawable.admin_portada,
                    com.flores.mycar.R.drawable.admin_defender,
                    com.flores.mycar.R.drawable.admin_cullinan,
                    com.flores.mycar.R.drawable.admin_catalogo
                ),
                titulo = "Panel del administrador",
                subtitulo = correo
            )

            // Totales
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Total("Usuarios", estado.usuarios, Icons.Filled.Groups, Modifier.weight(1f))
                Total("Pedidos", estado.pedidos, Icons.AutoMirrored.Filled.ReceiptLong, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Total("En proceso", estado.enProceso, Icons.Filled.HourglassTop, Modifier.weight(1f))
                Total("Citas por confirmar", estado.citasPendientes, Icons.AutoMirrored.Filled.EventNote, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Total("Marcas guardadas", estado.totalGuardado, Icons.Filled.Category, Modifier.weight(1f))
                Total("Favoritos", estado.totalFavoritos, Icons.Filled.Star, Modifier.weight(1f))
            }

            // Gestion
            Text("Gestión", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
            Accion("Pedidos y citas", "Cambia el estado de las compras y confirma citas", Icons.AutoMirrored.Filled.ReceiptLong, onGestion)
            Accion("Catálogo en venta", "Activa o desactiva marcas y autos", Icons.Filled.Storefront, onCatalogo)
            Accion("Promociones", "Descuentos por mes: crear, activar y desactivar", Icons.Filled.LocalOffer, onPromociones)
            Accion("Usuarios", "Quiénes están registrados", Icons.Filled.Groups, onUsuarios)

            // Datos
            Text("Datos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
            Button(
                onClick = viewModel::sincronizar,
                enabled = !estado.sincronizando,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(22.dp))
                Text(if (estado.sincronizando) "Sincronizando..." else "Sincronizar", modifier = Modifier.padding(start = 10.dp))
            }
            OutlinedButton(
                onClick = viewModel::limpiarCache,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(22.dp))
                Text("Limpiar caché", modifier = Modifier.padding(start = 10.dp))
            }

            estado.mensaje?.let {
                Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun Total(titulo: String, valor: Int, icono: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Text(androidx.compose.animation.core.animateIntAsState(valor, androidx.compose.animation.core.tween(900), label = "total").value.toString(), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
private fun Accion(titulo: String, detalle: String, icono: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(52.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
