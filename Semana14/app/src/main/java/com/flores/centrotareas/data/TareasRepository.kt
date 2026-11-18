package com.flores.centrotareas.data

// Commit 1: contrato del Repository (proyecto base)
interface TareasRepository {
    suspend fun contarPrimos(hasta: Int): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
