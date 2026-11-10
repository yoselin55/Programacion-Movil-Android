package com.flores.mycar.data.remote.wiki

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

/** API publica de Wikipedia (REST). No necesita clave. */
interface WikiApi {

    /** Resumen de un articulo: descripcion y foto principal. */
    @GET("api/rest_v1/page/summary/{titulo}")
    suspend fun resumen(@Path("titulo") titulo: String): WikiResumenDto

    /** Todas las imagenes de un articulo. */
    @GET("api/rest_v1/page/media-list/{titulo}")
    suspend fun medios(@Path("titulo") titulo: String): WikiMediosDto
}

@Serializable
data class WikiResumenDto(
    val type: String = "",
    val title: String = "",
    val extract: String? = null,
    val thumbnail: WikiImagenDto? = null,
    val originalimage: WikiImagenDto? = null
)

@Serializable
data class WikiImagenDto(val source: String)

@Serializable
data class WikiMediosDto(val items: List<WikiMedioDto> = emptyList())

@Serializable
data class WikiMedioDto(
    val type: String = "",
    val title: String = "",
    val srcset: List<WikiSrcDto> = emptyList()
)

@Serializable
data class WikiSrcDto(val src: String, val scale: String = "")
