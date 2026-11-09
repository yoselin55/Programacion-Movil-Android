package com.flores.mycar.data.repository

import com.flores.mycar.domain.model.Item
import kotlinx.coroutines.flow.Flow

/** Contrato del catalogo. Room es la fuente unica de verdad; Retrofit solo la actualiza. */
interface ItemRepository {
    val items: Flow<List<Item>>           // sale de Room
    val favoritos: Flow<List<Item>>
    suspend fun refrescar(): Result<Int>  // Retrofit -> Room; devuelve cuantos guardo
    suspend fun obtenerDetalle(id: Int): Result<Item>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}
