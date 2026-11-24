package com.flores.murocampus.data.remote

import com.flores.murocampus.model.Comentario
import com.flores.murocampus.model.NuevoPost
import com.flores.murocampus.model.Post
import retrofit2.http.Body
import retrofit2.http.GET
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
}
