package com.flores.lavacampus.ui.components

import androidx.compose.ui.text.style.TextAlign
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
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
    alineacion: Alignment = Alignment.Center, // que parte de la foto queda visible al recortar
    video: Int? = null, // recurso de res/raw: si se da, el video (en bucle) se reproduce sobre la foto
    inicioVelo: Float = 0f, // de 0 a 1: desde que altura empieza el color (para dejar sin tinte la parte de arriba de la foto)
    veloSuperior: Float = 0.35f, // que tan oscuro es el color arriba (0 a 1); abajo siempre es "intensidad"
    contenido: @Composable BoxScope.() -> Unit = {}
) {
    // En modo oscuro el primario es claro: se usa el contenedor oscuro para que el texto blanco se lea
    val colores = MaterialTheme.colorScheme
    val oscuro = colores.background.luminance() < 0.5f
    val color = if (oscuro || colores.primary.luminance() > 0.5f) colores.primaryContainer else colores.primary
    Box(modifier) {
        Image(
            painter = painterResource(recurso),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = alineacion,
            modifier = Modifier.fillMaxSize()
        )
        if (video != null) VideoFondo(video)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    if (inicioVelo > 0f) {
                        // La parte de arriba queda con la foto limpia y el color aparece hacia abajo
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            inicioVelo to Color.Transparent,
                            1f to color.copy(alpha = intensidad)
                        )
                    } else {
                        Brush.verticalGradient(listOf(color.copy(alpha = veloSuperior), color.copy(alpha = intensidad)))
                    }
                )
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
    alineacion: Alignment = Alignment.Center,
    derecha: @Composable () -> Unit = {}
) {
    FotoConDegradado(
        recurso,
        modifier.fillMaxWidth().height(alto).clip(RoundedCornerShape(22.dp)),
        alineacion = alineacion
    ) {
        Row(Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitulo, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
            }
            derecha()
        }
    }
}

// Video de fondo en bucle y sin sonido, con la foto debajo mientras carga (o si el video falla).
// TextureView + MediaPlayer vienen con Android: no hace falta ninguna libreria. Se recorta para llenar el espacio
@Composable
fun VideoFondo(recurso: Int, modifier: Modifier = Modifier) {
    var listo by remember { mutableStateOf(false) }
    Box(modifier.fillMaxSize().clipToBounds(), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { contexto ->
                TextureView(contexto).apply {
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        private var reproductor: MediaPlayer? = null

                        override fun onSurfaceTextureAvailable(st: SurfaceTexture, ancho: Int, alto: Int) {
                            reproductor = runCatching {
                                MediaPlayer().apply {
                                    contexto.resources.openRawResourceFd(recurso).use {
                                        setDataSource(it.fileDescriptor, it.startOffset, it.length)
                                    }
                                    setSurface(Surface(st))
                                    isLooping = true
                                    setVolume(0f, 0f)
                                    setOnPreparedListener { mp -> mp.start(); listo = true }
                                    setOnErrorListener { _, _, _ -> listo = false; true }
                                    prepareAsync()
                                }
                            }.getOrNull()
                        }

                        override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                            runCatching { reproductor?.release() }
                            reproductor = null
                            listo = false
                            return true
                        }

                        override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, ancho: Int, alto: Int) = Unit
                        override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit
                    }
                }
            },
            // El video es 16:9: se deja con su proporcion y lo que sobra arriba y abajo se recorta
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically, unbounded = true)
                .aspectRatio(16f / 9f)
                .alpha(if (listo) 1f else 0f)
        )
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

// Pantalla sin datos: foto de una pila de camisas planchadas y un texto amable
@Composable
fun EstadoVacio(titulo: String, detalle: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.foto_camisas),
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
