package com.flores.murocampus.data.remote

import com.flores.murocampus.model.Comentario
import com.flores.murocampus.model.EditarPost
import com.flores.murocampus.model.NuevoPost
import com.flores.murocampus.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Commit 4: interface Retrofit de Post con GET, POST, Path, Query y Body.
interface PostApi {
    @GET("posts")
    suspend fun listar(): List<Post>

    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post

    // IA commit 5: DELETE responde vacio; Response<Unit> deja revisar el codigo HTTP.
    @DELETE("posts/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>

    // IA commit 6: PATCH modifica solo los campos enviados; devuelve la publicacion actualizada.
    @PATCH("posts/{id}")
    suspend fun editar(@Path("id") id: Int, @Body cambios: EditarPost): Post
}
