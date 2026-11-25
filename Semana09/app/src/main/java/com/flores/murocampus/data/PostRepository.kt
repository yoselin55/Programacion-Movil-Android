package com.flores.murocampus.data

import com.flores.murocampus.data.remote.PostApi
import com.flores.murocampus.model.Comentario
import com.flores.murocampus.model.NuevoPost
import com.flores.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

// Commit 6: el Repository convierte toda excepcion de red en Result.failure.
interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun borrar(id: Int): Result<Unit>
}

class PostRepositoryRemote(private val api: PostApi) : PostRepository {

    override suspend fun listar(): Result<List<Post>> = llamar { api.listar() }

    override suspend fun detalle(id: Int): Result<Post> = llamar { api.detalle(id) }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> =
        llamar { api.comentarios(id) }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> =
        llamar { api.porUsuario(userId) }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = llamar { api.crear(nuevo) }

    // IA commit 5: se revisa el codigo de respuesta; si no es 2xx se convierte en Result.failure.
    override suspend fun borrar(id: Int): Result<Unit> = llamar {
        val respuesta = api.borrar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    // Un solo try/catch para las cinco funciones; la cancelacion nunca se traga.
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
