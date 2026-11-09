package com.flores.mycar.data.remote

import com.flores.mycar.data.remote.dto.ItemsResponseDto
import com.flores.mycar.data.remote.dto.ModelosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

/** Endpoints de la API publica de NHTSA (vPIC). Sin clave. */
interface ApiService {

    /** Lista: marcas de autos de pasajeros. */
    @GET("vehicles/GetMakesForVehicleType/car?format=json")
    suspend fun listar(): ItemsResponseDto

    /** Detalle: modelos de una marca. */
    @GET("vehicles/GetModelsForMakeId/{id}?format=json")
    suspend fun detalle(@Path("id") id: Int): ModelosResponseDto
}
