package com.flores.mycar.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Respuesta del detalle: los modelos de una marca (GetModelsForMakeId). */
@Serializable
data class ModelosResponseDto(
    @SerialName("Count") val total: Int = 0,
    @SerialName("Results") val resultados: List<ModeloDto> = emptyList()
)

@Serializable
data class ModeloDto(
    @SerialName("Make_ID") val marcaId: Int,
    @SerialName("Make_Name") val marcaNombre: String,
    @SerialName("Model_ID") val id: Int,
    @SerialName("Model_Name") val nombre: String
)
