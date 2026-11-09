package com.flores.mycar.data.repository

import com.flores.mycar.data.local.ItemDao
import com.flores.mycar.data.remote.ApiService
import com.flores.mycar.domain.model.Item
import com.flores.mycar.mapper.aDetalle
import com.flores.mycar.mapper.aEntity
import com.flores.mycar.mapper.aItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementacion del catalogo. Room es la fuente unica de verdad:
 * la lista que ve la persona sale siempre de Room y Retrofit solo la actualiza.
 */
class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {

    override val items: Flow<List<Item>> = dao.todos().map { lista -> lista.map { it.aItem() } }
    override val favoritos: Flow<List<Item>> = dao.favoritos().map { lista -> lista.map { it.aItem() } }

    override suspend fun refrescar(): Result<Int> = try {
        // Si se insertara sin cuidado, se perderian los favoritos. Se leen antes y se reaplican.
        val favoritos = dao.idsFavoritos().toSet()
        val nuevos = api.listar().resultados.map { it.aEntity(esFavorito = it.id in favoritos) }
        dao.guardarTodos(nuevos)
        Result.success(nuevos.size)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {   // sin internet (IOException), 404/500 (HttpException), JSON invalido...
        Result.failure(e)
    }

    /** Segundo endpoint: modelos de la marca. Conserva el favorito de la marca guardada en Room. */
    override suspend fun obtenerDetalle(id: Int): Result<Item> = try {
        val respuesta = api.detalle(id)
        Result.success(respuesta.aDetalle(id, dao.porId(id)?.aItem()))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada(): Int = dao.contar()
    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()
    override suspend fun limpiarCache() = dao.borrarTodo()
}
