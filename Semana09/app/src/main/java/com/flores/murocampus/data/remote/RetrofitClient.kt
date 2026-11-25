package com.flores.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Commit 5: cliente Retrofit único, con registro HTTP y conversor kotlinx.serialization.
object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }

    // IA commit 2: interceptor que agrega "Accept: application/json" a TODAS las peticiones.
    // Va antes del registro para que el encabezado tambien se vea en Logcat.
    private val encabezadoAccept = Interceptor { chain ->
        val peticion = chain.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        chain.proceed(peticion)
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(encabezadoAccept)
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    val api: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PostApi::class.java)
    }
}
