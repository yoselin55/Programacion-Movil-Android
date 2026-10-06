package com.flores.saludplus.ui.components

// Imágenes por nombre (ver IMAGENES.md en la raíz del proyecto). El nombre sale del texto de la
// interfaz con nombreRecurso: "Dra. Ana Torres" -> dra_ana_torres, "Pediatría" -> pediatria.
// - Médico con placeholder (p. ej. dr_jorge_salas.xml): borra el .xml y copia la foto real (.png, .jpg
//   o .webp) con el MISMO nombre en minúsculas. Si quedan los dos, Android da "Duplicate resources".
// - Especialidades: las 7 imágenes ya existen (pediatria.png...); si faltara una, se dibuja el respaldo.
// El código no cambia en ningún caso.

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.flores.saludplus.data.model.Especialidad
import com.flores.saludplus.ui.theme.AzulClaro
import com.flores.saludplus.ui.theme.AzulPrimario
import com.flores.saludplus.ui.theme.Celeste
import com.flores.saludplus.ui.theme.CelesteClaro
import com.flores.saludplus.ui.theme.Morado
import com.flores.saludplus.ui.theme.MoradoClaro
import com.flores.saludplus.ui.theme.Naranja
import com.flores.saludplus.ui.theme.NaranjaClaro
import com.flores.saludplus.ui.theme.Rojo
import com.flores.saludplus.ui.theme.RojoClaro
import com.flores.saludplus.ui.theme.Rosa
import com.flores.saludplus.ui.theme.RosaClaro
import com.flores.saludplus.ui.theme.VerdeClaro
import com.flores.saludplus.ui.theme.VerdeDisponible
import java.text.Normalizer

// Relaciones:
// - La usan SplashScreen (logo_saludplus, ilustracion_doctor), HomeScreen y ItemEspecialidad
//   (ImagenEspecialidad), TarjetaMedico, FechaHoraScreen y ConfirmarCitaScreen (FotoMedico)
// - Busca los recursos de app/src/main/res/drawable por nombre; los nombres salen de nombreRecurso
// - Lo prueba NombreRecursoTest (app/src/test)

// Fase 2: convierte un texto de la interfaz en nombre de recurso válido.
// Devuelve minúsculas sin tildes, ñ ni puntos, con "_" entre palabras ("Dr. Ricardo Núñez" -> "dr_ricardo_nunez").
fun nombreRecurso(texto: String): String =
    Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "")          // quita tildes y la virgulilla de la ñ
        .lowercase()
        .replace(".", "")                       // "Dra." -> "dra"
        .replace(Regex("[^a-z0-9]+"), "_")      // espacios y otros signos -> un solo "_"
        .trim('_')

// Fase 2: dibuja la imagen de res/drawable con el nombre indicado; si no existe, dibuja respaldo()
@SuppressLint("DiscouragedApi")
@Composable
fun ImagenPorNombre(
    nombre: String,
    descripcion: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    // Fase 2: alineación de la imagen dentro de su espacio (con valor por defecto, centrada)
    alineacion: Alignment = Alignment.Center,
    respaldo: @Composable () -> Unit
) {
    val contexto = LocalContext.current
    // Fase 2: getIdentifier busca el recurso por texto ("dra_ana_torres") en vez de R.drawable.x.
    // Así el nombre se arma desde los datos y, al cambiar el placeholder .xml por una foto
    // .png/.jpg/.webp con el mismo nombre, el código sigue funcionando sin cambios.
    val id = remember(nombre) {
        if (nombre.isBlank()) 0 else contexto.resources.getIdentifier(nombre, "drawable", contexto.packageName)
    }
    if (id == 0) {
        // Fase 2: el recurso no existe, se muestra el contenido de respaldo
        respaldo()
    } else {
        Image(
            painter = painterResource(id),
            contentDescription = descripcion,
            modifier = modifier,
            alignment = alineacion,
            contentScale = contentScale
        )
    }
}

// Fase 2: colores (pastel, fuerte) del respaldo de cada especialidad según su id (1 a 7)
private fun coloresEspecialidad(id: Int): Pair<Color, Color> = when (id) {
    1 -> AzulClaro to AzulPrimario       // Medicina General
    2 -> NaranjaClaro to Naranja         // Pediatría
    3 -> RosaClaro to Rosa               // Ginecología
    4 -> RojoClaro to Rojo               // Cardiología
    5 -> MoradoClaro to Morado           // Dermatología
    6 -> VerdeClaro to VerdeDisponible   // Traumatología
    7 -> CelesteClaro to Celeste         // Oftalmología
    else -> AzulClaro to AzulPrimario
}

// Fase 2: dibuja la imagen circular de una especialidad (nombreRecurso(nombre));
// si no hay imagen, un círculo pastel con el ícono de la especialidad en su color fuerte
@Composable
fun ImagenEspecialidad(especialidad: Especialidad, tamano: Dp, modifier: Modifier = Modifier) {
    ImagenPorNombre(
        nombre = nombreRecurso(especialidad.nombre),
        descripcion = especialidad.nombre,
        modifier = modifier.size(tamano).clip(CircleShape),
        respaldo = {
            val (pastel, fuerte) = coloresEspecialidad(especialidad.id)
            Box(
                modifier = modifier.size(tamano).background(pastel, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Ícono a ~55% del círculo
                Icon(especialidad.icono, contentDescription = null, tint = fuerte, modifier = Modifier.size(tamano * 0.55f))
            }
        }
    )
}

// Fase 2: dibuja la foto circular de un médico (nombreRecurso(nombre));
// si no hay imagen, un círculo azul claro con la silueta de persona
@Composable
fun FotoMedico(nombre: String, tamano: Dp, modifier: Modifier = Modifier) {
    ImagenPorNombre(
        nombre = nombreRecurso(nombre),
        descripcion = nombre,
        modifier = modifier.size(tamano).clip(CircleShape),
        respaldo = {
            Box(
                modifier = modifier.size(tamano).background(AzulClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(tamano * 0.55f))
            }
        }
    )
}
