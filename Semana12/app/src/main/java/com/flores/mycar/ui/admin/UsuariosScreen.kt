package com.flores.mycar.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.ui.components.Cargando
import com.flores.mycar.ui.components.EstadoVacio
import com.flores.mycar.ui.components.IconoAtras

/** Usuarios registrados: cuantos hay y quienes son. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    viewModel: UsuariosViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Usuarios") }, navigationIcon = { IconoAtras(onVolver) }) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            val lista = usuarios
            when {
                error != null -> EstadoVacio(Icons.Filled.Groups, "No se pudo cargar", error.orEmpty())
                lista == null -> Cargando()
                lista.isEmpty() -> EstadoVacio(Icons.Filled.Groups, "Todavía no hay usuarios", "Cuando alguien se registre, aparecerá aquí.")
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "${lista.count { it.rol == "usuario" }} usuarios registrados",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(lista, key = { it.uid }) { u ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(52.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            u.nombre.ifBlank { u.correo }.firstOrNull()?.uppercase() ?: "?",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                                    Text(u.nombre.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                                    if (u.correo.isNotBlank()) {
                                        Text(u.correo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    }
                                    if (u.ciudad.isNotBlank()) {
                                        Text(u.ciudad, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (u.rol == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        if (u.rol == "admin") "Admin" else "Cliente",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (u.rol == "admin") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
