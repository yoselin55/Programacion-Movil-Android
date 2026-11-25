package com.flores.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.murocampus.ui.theme.RosaClaro
import com.flores.murocampus.ui.theme.RosaOscuro
import com.flores.murocampus.ui.theme.RosaTexto

// IA commit 1: destinos de la barra lateral (se iran agregando con cada mejora).
enum class Destino(val etiqueta: String, val icono: ImageVector) {
    Inicio("Inicio", Icons.Default.Home),
    Crear("Crear", Icons.Default.Add)
}

// Barra lateral rosa con degradado, inspirada en el diseno de referencia.
@Composable
fun BarraLateral(
    destino: Destino,
    onDestino: (Destino) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(68.dp)
            .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(RosaClaro, RosaOscuro)))
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(16.dp))
        Destino.entries.forEach { d ->
            ItemNav(d, seleccionado = d == destino, onClick = { onDestino(d) })
        }
    }
}

@Composable
private fun ItemNav(destino: Destino, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionado) Color.White.copy(alpha = 0.32f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = destino.icono,
            contentDescription = destino.etiqueta,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Titulo de cada pantalla, en el estilo del diseno (texto rosa oscuro sobre fondo suave).
@Composable
fun Encabezado(titulo: String, subtitulo: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = titulo,
            color = RosaTexto,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = subtitulo, color = RosaTexto.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}
