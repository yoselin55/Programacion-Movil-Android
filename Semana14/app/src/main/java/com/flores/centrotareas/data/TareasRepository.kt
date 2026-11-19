package com.flores.centrotareas.data

import kotlinx.coroutines.flow.Flow

// Commit 1: contrato del Repository (proyecto base)
interface TareasRepository {
    // Commit 10 (Mejora 1): el progreso de la tarea sale del Repository como un Flow
    fun tareaConProgreso(): Flow<Float>

    suspend fun contarPrimos(hasta: Int): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
