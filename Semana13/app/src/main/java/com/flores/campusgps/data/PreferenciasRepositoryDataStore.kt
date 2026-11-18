package com.flores.campusgps.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Mejora 2: un solo DataStore para toda la app (archivo "campusgps_prefs").
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "campusgps_prefs")

// Máximo de elementos que se guardan en cada historial.
private const val MAXIMO_HISTORIAL = 5

// Mejora 2: guarda y lee el último lugar visitado con DataStore (solo aquí se usa DataStore).
// Mejora 3: también guarda el nombre, los lugares recientes y las búsquedas recientes.
class PreferenciasRepositoryDataStore(private val context: Context) : PreferenciasRepository {

    private val claveUltimoLugar = intPreferencesKey("ultimo_lugar")
    private val claveNombre = stringPreferencesKey("nombre")
    private val claveRecientes = stringPreferencesKey("recientes")   // ejemplo: "3,1,5"
    private val claveBusquedas = stringPreferencesKey("busquedas")   // una búsqueda por línea

    override val ultimoLugarId: Flow<Int?> =
        context.dataStore.data.map { it[claveUltimoLugar] }

    override suspend fun guardarUltimoLugar(id: Int) {
        context.dataStore.edit { it[claveUltimoLugar] = id }
    }

    override val nombre: Flow<String> =
        context.dataStore.data.map { it[claveNombre] ?: "" }

    override suspend fun guardarNombre(nombre: String) {
        context.dataStore.edit { it[claveNombre] = nombre }
    }

    override val recientes: Flow<List<Int>> =
        context.dataStore.data.map { leerIds(it[claveRecientes]) }

    // El lugar elegido pasa al principio; si ya estaba, se mueve (no se repite).
    override suspend fun agregarReciente(id: Int) {
        context.dataStore.edit { prefs ->
            val nuevos = (listOf(id) + leerIds(prefs[claveRecientes]).filter { it != id }).take(MAXIMO_HISTORIAL)
            prefs[claveRecientes] = nuevos.joinToString(",")
        }
    }

    override val busquedas: Flow<List<String>> =
        context.dataStore.data.map { leerBusquedas(it[claveBusquedas]) }

    override suspend fun agregarBusqueda(texto: String) {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return
        context.dataStore.edit { prefs ->
            val anteriores = leerBusquedas(prefs[claveBusquedas]).filter { !it.equals(limpio, ignoreCase = true) }
            prefs[claveBusquedas] = (listOf(limpio) + anteriores).take(MAXIMO_HISTORIAL).joinToString("\n")
        }
    }

    override suspend fun borrarHistorial() {
        context.dataStore.edit {
            it.remove(claveRecientes)
            it.remove(claveBusquedas)
            it.remove(claveUltimoLugar)
        }
    }

    private fun leerIds(texto: String?): List<Int> =
        texto.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }

    private fun leerBusquedas(texto: String?): List<String> =
        texto.orEmpty().split("\n").map { it.trim() }.filter { it.isNotEmpty() }
}
