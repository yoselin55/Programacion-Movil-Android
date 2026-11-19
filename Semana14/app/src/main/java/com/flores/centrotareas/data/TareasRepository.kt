package com.flores.centrotareas.data

import kotlinx.coroutines.flow.Flow

// Commit 1: contrato del Repository (proyecto base)
interface TareasRepository {
    // Commit 10 (Mejora 1): el progreso de la tarea sale del Repository como un Flow
    fun tareaConProgreso(): Flow<Float>

    // Commit 11 (Mejora 2): avisa el avance (0f a 1f) mientras cuenta
    suspend fun contarPrimos(hasta: Int, alAvanzar: (Float) -> Unit = {}): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
