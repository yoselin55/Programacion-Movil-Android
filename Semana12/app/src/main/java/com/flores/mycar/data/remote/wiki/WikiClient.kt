package com.flores.mycar.data.remote.wiki

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** Wikimedia pide que cada app se identifique con un User-Agent. */
const val USER_AGENT = "MycarApp/1.0 (proyecto academico Android)"

/** Crea los clientes de Wikipedia en español y en ingles. */
object WikiClient {

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            cadena.proceed(cadena.request().newBuilder().header("User-Agent", USER_AGENT).build())
        }
        .build()

    private fun crear(idioma: String): WikiApi =
        Retrofit.Builder()
            .baseUrl("https://$idioma.wikipedia.org/")
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WikiApi::class.java)

    val es: WikiApi by lazy { crear("es") }
    val en: WikiApi by lazy { crear("en") }

    /** Wikidata: logo oficial de cada marca. */
    val wikidata: WikidataApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wikidata.org/")
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WikidataApi::class.java)
    }
}
