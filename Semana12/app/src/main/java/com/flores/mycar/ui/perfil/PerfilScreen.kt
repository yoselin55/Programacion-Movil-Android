package com.flores.mycar.ui.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.domain.model.ModoTema
import com.flores.mycar.domain.model.Rol
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.components.Avatar
import com.flores.mycar.ui.components.BarraPrincipal

/** Perfil: foto, datos de la persona (editables), apariencia, mis pedidos y cerrar sesion. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    temaViewModel: TemaViewModel,
    sesion: Sesion,
    onMenu: () -> Unit,
    onMisPedidos: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val modo by temaViewModel.modo.collectAsStateWithLifecycle()
    LaunchedEffect(sesion.uid) { viewModel.usuario(sesion) }
    var editando by rememberSaveable { mutableStateOf(false) }

    // Selector de fotos del sistema: no necesita permisos.
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::cambiarFoto)
    }

    val avisos = remember { SnackbarHostState() }
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            viewModel.mensajeMostrado()
            avisos.showSnackbar(it)
        }
    }

    val nombre = estado.datos.nombre.ifBlank { sesion.nombre }

    Scaffold(
        modifier = modifier,
        topBar = { BarraPrincipal("Perfil", onMenu = onMenu) },
        snackbarHost = { SnackbarHost(avisos) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Foto y nombre
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(nombre = nombre.ifBlank { sesion.correo }, foto = estado.foto, tamano = 112)
                TextButton(
                    onClick = {
                        selectorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Cambiar foto de perfil", modifier = Modifier.padding(start = 8.dp))
                }
                Text(nombre.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.headlineSmall)
                Text(
                    sesion.correo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Datos personales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Mis datos", style = MaterialTheme.typography.titleLarge)
                        TextButton(onClick = { editando = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Editar", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                    Dato("Nombre", nombre.ifBlank { "Sin completar" })
                    Dato("Correo", sesion.correo)
                    Dato("Teléfono", estado.datos.telefono.ifBlank { "Sin completar" })
                    Dato("Ciudad", estado.datos.ciudad.ifBlank { "Sin completar" })
                    Dato("Tipo de cuenta", if (sesion.rol == Rol.ADMIN) "Administrador" else "Cliente", ultimo = true)
                }
            }

            // Apariencia
            Column {
                Text("Apariencia", style = MaterialTheme.typography.titleLarge)
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OpcionTema("Sistema", Icons.Filled.SettingsBrightness, modo == ModoTema.SISTEMA) { temaViewModel.cambiar(ModoTema.SISTEMA) }
                    OpcionTema("Claro", Icons.Filled.LightMode, modo == ModoTema.CLARO) { temaViewModel.cambiar(ModoTema.CLARO) }
                    OpcionTema("Oscuro", Icons.Filled.DarkMode, modo == ModoTema.OSCURO) { temaViewModel.cambiar(ModoTema.OSCURO) }
                }
            }

            if (sesion.rol != Rol.ADMIN) {
                OutlinedButton(
                    onClick = onMisPedidos,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Filled.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Mis pedidos y citas", modifier = Modifier.padding(start = 8.dp))
                }
            }
            Button(
                onClick = onCerrarSesion,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Cerrar sesión", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    if (editando) {
        var n by rememberSaveable { mutableStateOf(nombre) }
        var t by rememberSaveable { mutableStateOf(estado.datos.telefono) }
        var c by rememberSaveable { mutableStateOf(estado.datos.ciudad) }
        AlertDialog(
            onDismissRequest = { editando = false },
            icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            title = { Text("Editar mis datos") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Nombre") }, singleLine = true)
                    OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("Teléfono") }, singleLine = true)
                    OutlinedTextField(value = c, onValueChange = { c = it }, label = { Text("Ciudad") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = { editando = false; viewModel.guardarDatos(n, t, c) }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editando = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String, ultimo: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.titleMedium)
    }
    if (!ultimo) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun OpcionTema(etiqueta: String, icono: ImageVector, seleccionada: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionada,
        onClick = onClick,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp)) }
    )
}
