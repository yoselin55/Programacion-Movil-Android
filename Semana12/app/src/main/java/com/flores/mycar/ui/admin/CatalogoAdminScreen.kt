package com.flores.mycar.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.ui.components.FotoAuto
import com.flores.mycar.ui.components.IconoAtras

/** Catalogo en venta: el administrador activa o desactiva marcas completas o autos sueltos. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoAdminScreen(
    viewModel: CatalogoAdminViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val marcas by viewModel.marcas.collectAsStateWithLifecycle()
    val apagados by viewModel.apagados.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    var abierta by rememberSaveable { mutableStateOf(-1) }

    val avisos = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            viewModel.mensajeMostrado()
            avisos.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Catálogo en venta") }, navigationIcon = { IconoAtras(onVolver) }) },
        snackbarHost = { SnackbarHost(avisos) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(24.dp))) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(com.flores.mycar.R.drawable.admin_catalogo),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(0.3f to androidx.compose.ui.graphics.Color.Transparent, 1f to androidx.compose.ui.graphics.Color(0xCC11120D))))
                Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                    Text("Catálogo en venta", style = MaterialTheme.typography.headlineSmall, color = androidx.compose.ui.graphics.Color(0xFFFFFBF4))
                    Text("Apaga una marca o un auto y los usuarios dejarán de verlo.", style = MaterialTheme.typography.bodyMedium, color = androidx.compose.ui.graphics.Color(0xFFFFFBF4))
                }
            }
            }
            items(marcas, key = { it.id }) { marca ->
                val marcaActiva = "marca_${marca.id}" !in apagados
                val activos = marca.autos.count { "auto_${it.id}" !in apagados }
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth().animateContentSize()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                                Text(marca.nombre, style = MaterialTheme.typography.titleLarge, maxLines = 1)
                                Text(
                                    if (marcaActiva) "$activos de ${marca.autos.size} autos en venta" else "Marca desactivada",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (marcaActiva) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                )
                            }
                            Switch(checked = marcaActiva, onCheckedChange = { viewModel.alternarMarca(marca, it) })
                        }
                        TextButton(onClick = { abierta = if (abierta == marca.id) -1 else marca.id }, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(if (abierta == marca.id) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                            Text(if (abierta == marca.id) "Ocultar autos" else "Ver autos", modifier = Modifier.padding(start = 6.dp))
                        }
                        AnimatedVisibility(visible = abierta == marca.id) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (marca.autos.isEmpty()) {
                                    Text("Cargando autos...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                marca.autos.forEach { auto ->
                                    val activo = "auto_${auto.id}" !in apagados
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        FotoAuto(auto.imagenUrl, Modifier.size(width = 72.dp, height = 54.dp).clip(RoundedCornerShape(12.dp)))
                                        Text(
                                            auto.modelo.ifBlank { auto.marca },
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 2,
                                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                                        )
                                        Switch(checked = activo && marcaActiva, enabled = marcaActiva, onCheckedChange = { viewModel.alternarAuto(auto, it) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
