package com.flores.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.flores.lavacampus.data.local.entity.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {
    @Insert
    suspend fun insertar(n: Notificacion): Long

    @Insert
    suspend fun insertarTodas(lista: List<Notificacion>)

    // Las mas recientes primero. Flow: la lista y el contador se actualizan solos
    @Query("SELECT * FROM notificaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC, id DESC")
    fun observarDeUsuario(usuarioId: Int): Flow<List<Notificacion>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE usuarioId = :usuarioId AND leida = 0")
    fun observarNoLeidas(usuarioId: Int): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Int)

    @Query("UPDATE notificaciones SET leida = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodasLeidas(usuarioId: Int)

    @Query("DELETE FROM notificaciones WHERE usuarioId = :usuarioId")
    suspend fun borrarDeUsuario(usuarioId: Int)
}
