package com.flores.murocampus.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.murocampus.ui.theme.FuenteTitulos
import com.flores.murocampus.ui.theme.RosaClaro
import com.flores.murocampus.ui.theme.RosaOscuro
import com.flores.murocampus.ui.theme.RosaPastel
import com.flores.murocampus.ui.theme.RosaTexto

// Destinos de la barra lateral (se iran agregando con cada mejora).
enum class Destino(val etiqueta: String, val icono: ImageVector) {
    Inicio("Inicio", Icons.Default.Home),
    Crear("Crear", Icons.Default.Add)
}

// IA commit 4: las tres rayitas abren y cierran el menu. Abierto muestra icono y nombre;
// cerrado solo el icono. El ancho cambia con una animacion.
@Composable
fun BarraLateral(
    destino: Destino,
    onDestino: (Destino) -> Unit,
    expandida: Boolean,
    onAlternar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ancho by animateDpAsState(if (expandida) 176.dp else 68.dp, label = "ancho")

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(ancho)
            .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(RosaClaro, RosaOscuro)))
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 11.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ItemNav(
            icono = if (expandida) Icons.Default.Close else Icons.Default.Menu,
            etiqueta = "Cerrar menú",
            expandida = expandida,
            seleccionado = false,
            textoVisible = false,
            onClick = onAlternar
        )
        Spacer(Modifier.height(10.dp))
        Destino.entries.forEach { d ->
            ItemNav(
                icono = d.icono,
                etiqueta = d.etiqueta,
                expandida = expandida,
                seleccionado = d == destino,
                textoVisible = true,
                onClick = { onDestino(d) }
            )
        }
    }
}

@Composable
private fun ItemNav(
    icono: ImageVector,
    etiqueta: String,
    expandida: Boolean,
    seleccionado: Boolean,
    textoVisible: Boolean,
    onClick: () -> Unit
) {
    val fuente = remember { MutableInteractionSource() }
    val encima by fuente.collectIsHoveredAsState()
    val fondo by animateColorAsState(
        when {
            seleccionado -> Color.White.copy(alpha = 0.32f)
            encima -> Color.White.copy(alpha = 0.18f)
            else -> Color.Transparent
        },
        label = "fondoItem"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .hoverable(fuente)
            .clickable(interactionSource = fuente, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(46.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icono,
                contentDescription = etiqueta,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        if (expandida && textoVisible) {
            Text(
                text = etiqueta,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

// Titulo de cada pantalla: serif elegante y grande. Con onVolver muestra una flecha para regresar.
@Composable
fun Encabezado(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    onVolver: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = if (onVolver != null) 8.dp else 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onVolver != null) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier.size(46.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = RosaPastel,
                    contentColor = RosaTexto
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
        }
        Column {
            Text(
                text = titulo,
                color = RosaTexto,
                fontFamily = FuenteTitulos,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = subtitulo, color = RosaTexto.copy(alpha = 0.8f), fontSize = 16.sp)
        }
    }
}
