package com.flores.murocampus.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.murocampus.model.Post
import com.flores.murocampus.ui.theme.FuenteTitulos
import com.flores.murocampus.ui.theme.RosaClaro
import com.flores.murocampus.ui.theme.RosaOscuro
import com.flores.murocampus.ui.theme.RosaPastel

// Componentes visuales del proyecto base (TarjetaPost, Cargando, ErrorConReintento).

private val ColoresUsuario = listOf(
    Color(0xFFD98895), // rosa
    Color(0xFFE0A458), // durazno
    Color(0xFF7FB28F), // verde suave
    Color(0xFF9B86C9), // lavanda
    Color(0xFFC96A5E)  // coral
)

// IA commit 4: tarjeta con movimiento. Al pasar el mouse (o al presionar) cambia de color,
// se eleva y crece un poco. Con onClick es clicable; sin onClick solo reacciona al mouse.
@Composable
fun TarjetaInteractiva(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colorBase: Color = Color.White,
    colorHover: Color = RosaPastel,
    content: @Composable ColumnScope.() -> Unit
) {
    val fuente = remember { MutableInteractionSource() }
    val encima by fuente.collectIsHoveredAsState()
    val presionada by fuente.collectIsPressedAsState()
    val activa = encima || presionada

    val fondo by animateColorAsState(if (activa) colorHover else colorBase, label = "fondo")
    val escala by animateFloatAsState(if (activa) 1.015f else 1f, label = "escala")

    val base = modifier.fillMaxWidth().scale(escala).hoverable(fuente)
    val forma = RoundedCornerShape(20.dp)
    val colores = CardDefaults.cardColors(containerColor = fondo)
    val elevacion = CardDefaults.cardElevation(defaultElevation = if (activa) 6.dp else 1.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = base,
            shape = forma,
            colors = colores,
            elevation = elevacion,
            interactionSource = fuente,
            content = content
        )
    } else {
        Card(
            modifier = base,
            shape = forma,
            colors = colores,
            elevation = elevacion,
            content = content
        )
    }
}

@Composable
fun TarjetaPost(post: Post, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    TarjetaInteractiva(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarUsuario(post.userId)
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColoresUsuario[(post.userId - 1).coerceAtLeast(0) % ColoresUsuario.size]
                ) {
                    Text(
                        text = "Usuario ${post.userId}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = post.title,
                    fontFamily = FuenteTitulos,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF3B2A30),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = post.body.replace("\n", " "),
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Avatar circular con degradado (luego mostrara la foto de perfil).
@Composable
fun AvatarUsuario(userId: Int, modifier: Modifier = Modifier, tamano: Dp = 46.dp) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(RosaClaro, RosaOscuro))),
        contentAlignment = Alignment.Center
    ) {
        Text("U$userId", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            Text("Cargando publicaciones…", fontSize = 16.sp)
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
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}
