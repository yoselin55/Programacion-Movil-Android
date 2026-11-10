package com.flores.mycar.data.perfil

import android.content.Context
import android.net.Uri
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** La foto de perfil elegida; "version" cambia cada vez que se reemplaza para que se recargue. */
data class FotoPerfil(val archivo: File, val version: Long)

/** Foto de perfil de cada persona, guardada en el almacenamiento de la app. */
interface FotoPerfilRepository {
    fun foto(uid: String): Flow<FotoPerfil?>
    suspend fun guardar(uid: String, origen: Uri): Result<Unit>
}

class FotoPerfilArchivos(private val context: Context) : FotoPerfilRepository {

    private val version = MutableStateFlow(0L)

    private fun archivo(uid: String) = File(context.filesDir, "perfil_$uid.jpg")

    override fun foto(uid: String): Flow<FotoPerfil?> = version.map {
        archivo(uid).takeIf { it.exists() }?.let { FotoPerfil(it, it.lastModified()) }
    }

    override suspend fun guardar(uid: String, origen: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(origen).use { entrada ->
                requireNotNull(entrada) { "No se pudo abrir la imagen" }
                archivo(uid).outputStream().use { salida -> entrada.copyTo(salida) }
            }
            version.value = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
