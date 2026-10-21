package com.flores.lavacampus.ui.components

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.lavacampus.R
import com.flores.lavacampus.data.prefs.FotoPerfil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Foto de fondo con un degradado del color del perfil encima (para que el texto se lea)
@Composable
fun FotoConDegradado(
    recurso: Int,
    modifier: Modifier = Modifier,
    intensidad: Float = 0.85f,
    contenido: @Composable BoxScope.() -> Unit = {}
) {
    // En modo oscuro el primario es claro: se usa el contenedor oscuro para que el texto blanco se lea
    val primario = MaterialTheme.colorScheme.primary
    val color = if (primario.luminance() > 0.5f) MaterialTheme.colorScheme.primaryContainer else primario
    Box(modifier) {
        Image(
            painter = painterResource(recurso),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.35f), color.copy(alpha = intensidad))))
        )
        contenido()
    }
}

// Banner de pantalla: foto con degradado, titulo, subtitulo y un contenido opcional a la derecha
@Composable
fun BannerPantalla(
    recurso: Int,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    alto: Dp = 130.dp,
    derecha: @Composable () -> Unit = {}
) {
    FotoConDegradado(recurso, modifier.fillMaxWidth().height(alto).clip(RoundedCornerShape(22.dp))) {
        Row(Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitulo, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
            }
            derecha()
        }
    }
}

// Logo de LavaCampus (cuadro redondeado con esquinas transparentes)
@Composable
fun LogoLavaCampus(tamano: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_lavacampus),
        contentDescription = "Logo de LavaCampus",
        modifier = modifier.size(tamano)
    )
}

// Foto que corresponde al icono del servicio (plancha, tintoreria o lavadoras)
fun fotoDeServicio(icono: String): Int = when (icono) {
    "iron" -> R.drawable.foto_planchado
    "shirt" -> R.drawable.foto_lavado_seco
    else -> R.drawable.foto_lavanderia
}

// Foto del servicio con su icono dibujado con Canvas en una esquina
@Composable
fun FotoServicio(icono: String, tamano: Dp, modifier: Modifier = Modifier) {
    val recurso = fotoDeServicio(icono)
    Box(modifier.size(tamano)) {
        Image(
            painter = painterResource(recurso),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(tamano * 0.42f)
                .clip(CircleShape)
                .background(Color.White)
                .padding(tamano * 0.06f),
            contentAlignment = Alignment.Center
        ) { IconoServicio(icono, MaterialTheme.colorScheme.primary, tamano * 0.28f) }
    }
}

// Avatar del usuario: su foto si la subio; si no, sus iniciales
@Composable
fun AvatarUsuario(nombre: String, foto: FotoPerfil?, tamano: Dp, modifier: Modifier = Modifier) {
    val imagen by produceState<ImageBitmap?>(null, foto?.ruta, foto?.version) {
        value = foto?.let { f ->
            withContext(Dispatchers.IO) {
                android.graphics.BitmapFactory.decodeFile(f.ruta)?.asImageBitmap()
            }
        }
    }
    Box(
        modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        val actual = imagen
        if (actual != null) {
            Image(
                bitmap = actual,
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                nombre.iniciales(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = (tamano.value * 0.36f).sp,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

// Pantalla sin datos: foto de una canasta de ropa y un texto amable
@Composable
fun EstadoVacio(titulo: String, detalle: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.foto_canasta),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 170.dp, height = 110.dp).clip(RoundedCornerShape(20.dp))
        )
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            detalle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

// Pie de pagina con el logo (Perfil y Ajustes)
@Composable
fun PieLogo() {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoLavaCampus(48.dp)
        Column(Modifier.padding(start = 12.dp)) {
            Text("LavaCampus", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Versión 1.0 · Tecsup",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
