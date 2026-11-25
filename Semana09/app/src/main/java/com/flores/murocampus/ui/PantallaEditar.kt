package com.flores.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.murocampus.model.Post
import com.flores.murocampus.ui.theme.VerdeClaro
import com.flores.murocampus.viewmodel.PostsViewModel
import com.flores.murocampus.viewmodel.UiState

// IA commit 6: pantalla para editar el titulo (PATCH) y mostrar la respuesta del servidor.
// La pantalla solo dibuja el estado; el ViewModel hace el pedido a traves del Repository.
@Composable
fun PantallaEditar(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val detalle by vm.detalle.collectAsStateWithLifecycle()
    val edicion by vm.edicion.collectAsStateWithLifecycle()

    val post = (detalle as? UiState.Exito)?.datos?.post
    var titulo by rememberSaveable(post?.id) { mutableStateOf(post?.title ?: "") }
    val enviando = edicion is UiState.Cargando

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Nuevo título") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { if (post != null) vm.editar(post.id, titulo.trim()) },
            enabled = post != null && titulo.isNotBlank() && !enviando
        ) {
            Text(if (enviando) "Guardando…" else "Guardar cambios")
        }

        when (val e = edicion) {
            is UiState.Exito -> RespuestaServidor(e.datos)
            is UiState.Error -> Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFBE4E4),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = e.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> Unit
        }
    }
}

// Muestra lo que respondio el servidor al PATCH (el PDF pide "mostrar la respuesta").
@Composable
private fun RespuestaServidor(post: Post) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VerdeClaro,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Título actualizado",
                color = Color(0xFF1B5E20),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text("Respuesta del servidor (PATCH)", fontSize = 14.sp, color = Color(0xFF1B5E20))
            Text("id: ${post.id} · userId: ${post.userId}", fontSize = 15.sp)
            Text("title: ${post.title}", fontSize = 15.sp)
        }
    }
}
