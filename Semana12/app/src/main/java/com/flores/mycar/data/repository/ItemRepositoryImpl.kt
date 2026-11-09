package com.flores.mycar.data.repository

import com.flores.mycar.data.remote.ApiService
import com.flores.mycar.domain.model.Item
import com.flores.mycar.mapper.aItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Implementacion del catalogo. En este hito guarda los datos en memoria;
 * mas adelante se cambia a Room como fuente de verdad.
 */
class ItemRepositoryImpl(
    private val api: ApiService
) : ItemRepository {

    private val lista = MutableStateFlow<List<Item>>(emptyList())

    override val items: Flow<List<Item>> = lista.asStateFlow()
    override val favoritos: Flow<List<Item>> = lista.map { todos -> todos.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> = try {
        // Los favoritos no se pierden al sincronizar: se leen antes y se reaplican.
        val ids = lista.value.filter { it.esFavorito }.map { it.id }.toSet()
        val nuevos = api.listar().resultados
            .map { it.aItem(esFavorito = it.id in ids) }
            .sortedBy { it.titulo }
        lista.value = nuevos
        Result.success(nuevos.size)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {   // sin internet (IOException), 404/500 (HttpException), JSON invalido...
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> =
        Result.failure(UnsupportedOperationException("El detalle se implementa en el siguiente hito"))

    override suspend fun alternarFavorito(id: Int) {
        lista.update { todos -> todos.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it } }
    }

    override suspend fun cantidadGuardada(): Int = lista.value.size
    override suspend fun cantidadFavoritos(): Int = lista.value.count { it.esFavorito }
    override suspend fun limpiarCache() { lista.value = emptyList() }
}
