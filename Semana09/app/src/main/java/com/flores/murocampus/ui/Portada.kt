package com.flores.murocampus.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.murocampus.R
import com.flores.murocampus.ui.theme.FuenteTitulos
import com.flores.murocampus.ui.theme.RosaOscuro
import com.flores.murocampus.ui.theme.RosaTexto

// IA commit 3: portada con foto del campus, estilo "banner" de la referencia.
@Composable
fun PortadaHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.campus_tecsup),
            contentDescription = "Campus de TECSUP",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Degradado vino de izquierda a derecha para que el texto se lea sobre la foto.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            RosaOscuro.copy(alpha = 0.95f),
                            RosaOscuro.copy(alpha = 0.70f),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.72f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "TU MURO DEL CAMPUS",
                color = Color.White,
                fontFamily = FuenteTitulos,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Consulta, comparte y entérate de lo que pasa en TECSUP",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Surface(shape = RoundedCornerShape(50), color = Color.White) {
                Text(
                    text = "Publicaciones de la comunidad",
                    color = RosaOscuro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private data class Lugar(val nombre: String, val detalle: String, @DrawableRes val imagen: Int)

private val Lugares = listOf(
    Lugar("Campus", "Vida estudiantil", R.drawable.campus_tecsup),
    Lugar("Biblioteca", "Lee y comparte", R.drawable.biblioteca),
    Lugar("Laboratorios", "Practica y crea", R.drawable.laboratorio)
)

// IA commit 3: fila horizontal de tarjetas con foto (como "Bestsellers" de la referencia).
@Composable
fun ExploraCampus(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column {
            Text(
                text = "Explora el campus",
                color = RosaTexto,
                fontFamily = FuenteTitulos,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "¿Qué te gustaría consultar hoy?",
                color = RosaTexto.copy(alpha = 0.7f),
                fontSize = 15.sp
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Lugares) { TarjetaLugar(it) }
        }
    }
}

@Composable
private fun TarjetaLugar(lugar: Lugar) {
    Box(
        modifier = Modifier
            .width(132.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(lugar.imagen),
            contentDescription = lugar.nombre,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, RosaOscuro.copy(alpha = 0.85f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(lugar.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(lugar.detalle, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }
    }
}
