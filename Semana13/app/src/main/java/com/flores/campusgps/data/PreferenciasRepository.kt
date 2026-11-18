package com.flores.campusgps.data

import kotlinx.coroutines.flow.Flow

// Mejora 2 (último lugar visitado): contrato para guardar preferencias del usuario.
// El ViewModel solo conoce esta interfaz, no DataStore.
interface PreferenciasRepository {
    // Id del último lugar seleccionado; null si todavía no se eligió ninguno.
    val ultimoLugarId: Flow<Int?>

    suspend fun guardarUltimoLugar(id: Int)

    // Mejora 3 (bienvenida): nombre del usuario; texto vacío si todavía no lo escribió.
    val nombre: Flow<String>

    suspend fun guardarNombre(nombre: String)

    // Mejora 3 (recientes): ids de los últimos lugares visitados, del más nuevo al más viejo (máximo 5).
    val recientes: Flow<List<Int>>

    suspend fun agregarReciente(id: Int)

    // Mejora 3 (búsqueda): últimas palabras que el usuario buscó, de la más nueva a la más vieja (máximo 5).
    val busquedas: Flow<List<String>>

    suspend fun agregarBusqueda(texto: String)

    // Mejora 3 (perfil): borra los lugares recientes y las búsquedas guardadas.
    suspend fun borrarHistorial()
}
