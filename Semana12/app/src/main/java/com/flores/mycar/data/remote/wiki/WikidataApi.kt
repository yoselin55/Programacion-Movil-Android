package com.flores.mycar.data.remote.wiki

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Query

/** Wikidata: sabe cual es el logo oficial (propiedad P154) de cada empresa. Sin clave. */
interface WikidataApi {

    /** Busca entidades por nombre (ej. "Toyota"). */
    @GET("w/api.php?action=wbsearchentities&language=en&type=item&limit=8&format=json")
    suspend fun buscar(@Query("search") texto: String): WdBusquedaDto

    /** Pide solo el logo (P154) de una entidad. */
    @GET("w/api.php?action=wbgetclaims&format=json&property=P154")
    suspend fun logo(@Query("entity") entidad: String): WdClaimsDto
}

@Serializable
data class WdBusquedaDto(val search: List<WdEntidadDto> = emptyList())

@Serializable
data class WdEntidadDto(val id: String = "", val description: String = "")

@Serializable
data class WdClaimsDto(val claims: Map<String, List<WdClaimDto>> = emptyMap())

@Serializable
data class WdClaimDto(val mainsnak: WdSnakDto = WdSnakDto())

@Serializable
data class WdSnakDto(val datavalue: WdValorDto? = null)

@Serializable
data class WdValorDto(val value: JsonElement? = null)
