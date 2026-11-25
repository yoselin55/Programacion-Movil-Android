package com.flores.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
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
    modifier: Modifier = Modifier
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()

    when (val e = estado) {
        is UiState.Cargando -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { Cargando() }

        is UiState.Error -> Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) { ErrorConReintento(mensaje = e.mensaje, onReintentar = { vm.abrir(idPost) }) }

        is UiState.Exito -> ContenidoDetalle(e.datos, modifier)
    }
}

// IA commit 4: letras mas grandes, titulos en serif y comentarios que reaccionan al mouse.
@Composable
private fun ContenidoDetalle(detalle: DetalleUi, modifier: Modifier = Modifier) {
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
