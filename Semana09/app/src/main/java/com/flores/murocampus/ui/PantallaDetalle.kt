package com.flores.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.murocampus.model.Comentario
import com.flores.murocampus.ui.theme.FuenteTitulos
import com.flores.murocampus.viewmodel.DetalleUi
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// Commit 8: detalle de una publicacion con sus comentarios (solo dibuja el estado).
@Composable
fun PantallaDetalle(
    vm: PostsViewModel,
    idPost: Int,
    onBorrado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrado by vm.borrado.collectAsStateWithLifecycle()

    // IA commit 5: cuando el DELETE sale bien, se vuelve a la lista.
    LaunchedEffect(borrado) {
        if (borrado is UiState.Exito) {
            vm.reiniciarBorrado()
            onBorrado()
        }
    }

    when (val e = estado) {
        is UiState.Cargando -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { Cargando() }

        is UiState.Error -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.abrir(idPost) }) }

        is UiState.Exito -> ContenidoDetalle(
            detalle = e.datos,
            borrado = borrado,
            onEliminar = { vm.borrar(idPost) },
            modifier = modifier
        )
    }
}

// IA commit 4: letras mas grandes, titulos en serif y comentarios que reaccionan al mouse.
@Composable
private fun ContenidoDetalle(
    detalle: DetalleUi,
    borrado: UiState<Unit>?,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TarjetaInteractiva {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = detalle.post.title,
                        fontFamily = FuenteTitulos,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = Color(0xFF3B2A30)
                    )
                    Text(
                        text = detalle.post.body,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        item { BotonEliminar(borrado = borrado, onConfirmar = onEliminar) }
        item {
            Text(
                text = "Comentarios (${detalle.comentarios.size})",
                fontFamily = FuenteTitulos,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        items(detalle.comentarios, key = { it.id }) { TarjetaComentario(it) }
    }
}

@Composable
private fun TarjetaComentario(comentario: Comentario) {
    TarjetaInteractiva {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = comentario.email,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = comentario.body.replace("\n", " "),
                fontSize = 16.sp,
                lineHeight = 23.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// IA commit 5: boton Eliminar con confirmacion. Muestra "Eliminando..." y el error si falla.
@Composable
private fun BotonEliminar(borrado: UiState<Unit>?, onConfirmar: () -> Unit) {
    var confirmando by rememberSaveable { mutableStateOf(false) }
    val enviando = borrado is UiState.Cargando

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
            onClick = { confirmando = true },
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (enviando) "Eliminando…" else "Eliminar publicación", fontSize = 16.sp)
        }
        if (borrado is UiState.Error) {
            Text(
                text = borrado.mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp
            )
        }
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("¿Eliminar publicación?") },
            text = { Text("Se quitará de tu lista de publicaciones.") },
            confirmButton = {
                TextButton(onClick = { confirmando = false; onConfirmar() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) { Text("Cancelar") }
            }
        )
    }
}
