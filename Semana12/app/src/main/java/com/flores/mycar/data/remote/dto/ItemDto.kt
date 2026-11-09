package com.flores.mycar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Envoltorio de la API de NHTSA: la lista viene dentro de "Results". */
@Serializable
data class ItemsResponseDto(
    @SerialName("Count") val total: Int = 0,
    @SerialName("Results") val resultados: List<ItemDto> = emptyList()
)

/** Una marca de autos tal como la devuelve la API (GetMakesForVehicleType). */
@Serializable
data class ItemDto(
    @SerialName("MakeId") val id: Int,
    @SerialName("MakeName") val nombre: String,
    @SerialName("VehicleTypeName") val tipo: String? = null
)
