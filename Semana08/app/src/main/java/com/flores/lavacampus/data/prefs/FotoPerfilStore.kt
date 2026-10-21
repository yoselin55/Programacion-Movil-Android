package com.flores.lavacampus.data.prefs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// Ruta de la foto y su fecha: si la foto cambia, cambia la version y la pantalla la vuelve a cargar
data class FotoPerfil(val ruta: String, val version: Long)

// Guarda la foto de perfil de cada usuario en el almacenamiento interno de la app (privado).
// Es un archivo por usuario; no va en Room porque no es un dato relacional.
class FotoPerfilStore(private val context: Context) {

    private fun archivo(usuarioId: Int) = File(context.filesDir, "perfil_$usuarioId.jpg")

    fun foto(usuarioId: Int): FotoPerfil? =
        archivo(usuarioId).takeIf { it.exists() }?.let { FotoPerfil(it.absolutePath, it.lastModified()) }

    fun quitar(usuarioId: Int) {
        archivo(usuarioId).delete()
    }

    // Copia la imagen elegida: la reduce, corrige la rotacion, la recorta cuadrada y la guarda
    suspend fun guardar(usuarioId: Int, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver

            // 1) Solo se lee el tamano, para no cargar en memoria una foto enorme
            val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }
            var reduccion = 1
            while (medidas.outWidth / reduccion > 1024 || medidas.outHeight / reduccion > 1024) reduccion *= 2

            // 2) Se lee la imagen ya reducida
            val original = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = reduccion })
            } ?: return@runCatching false

            // 3) Rotacion segun la orientacion con que se tomo la foto
            val orientacion = resolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL
            val grados = when (orientacion) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            val girada = if (grados == 0f) original else Bitmap.createBitmap(
                original, 0, 0, original.width, original.height, Matrix().apply { postRotate(grados) }, true
            )

            // 4) Recorte cuadrado centrado de 512 px
            val lado = minOf(girada.width, girada.height)
            val cuadrada = Bitmap.createBitmap(girada, (girada.width - lado) / 2, (girada.height - lado) / 2, lado, lado)
            val final = Bitmap.createScaledBitmap(cuadrada, 512, 512, true)

            // 5) Se escribe en un archivo temporal y luego se reemplaza, para no dejar una foto a medias
            val temporal = File(context.filesDir, "perfil_$usuarioId.tmp")
            temporal.outputStream().use { final.compress(Bitmap.CompressFormat.JPEG, 88, it) }
            val destino = archivo(usuarioId)
            destino.delete()
            temporal.renameTo(destino)
        }.getOrDefault(false)
    }
}
