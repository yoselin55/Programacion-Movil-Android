package com.flores.murocampus.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.murocampus.model.Post

// Commit extra: componentes visuales del proyecto base (TarjetaPost, Cargando, ErrorConReintento).

private val ColoresUsuario = listOf(
    Color(0xFF2F80D0), // azul
    Color(0xFFF0A30A), // ambar
    Color(0xFF2E9E5B), // verde
    Color(0xFF8E5BD0), // violeta
    Color(0xFFD0503A)  // rojo
)

@Composable
fun TarjetaPost(post: Post, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val clic = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(
        modifier = modifier.fillMaxWidth().then(clic),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F6))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ColoresUsuario[(post.userId - 1).coerceAtLeast(0) % ColoresUsuario.size]
            ) {
                Text(
                    text = "Usuario ${post.userId}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = post.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1F2430),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = post.body.replace("\n", " "),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun Cargando(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
            Text("Cargando publicaciones…", fontSize = 13.sp)
        }
    }
}

@Composable
fun ErrorConReintento(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFBE4E4),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}
