package com.flores.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore declarado una sola vez, a nivel de archivo
private val Context.dataStore by preferencesDataStore("ajustes")

// Solo preferencias pequenas (clave-valor). Pedidos, servicios y usuarios van en Room
class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")   // CLIENTE | ADMIN | vacio
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibirPedidos")
    }

    // Lectura: un Flow por cada preferencia
    val perfilActivo: Flow<String> = context.dataStore.data.map { it[Claves.PERFIL_ACTIVO] ?: "" }
    val usuarioId: Flow<Int> = context.dataStore.data.map { it[Claves.USUARIO_ID] ?: 0 }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[Claves.MODO_OSCURO] ?: false }
    val notificaciones: Flow<Boolean> =
        context.dataStore.data.map { it[Claves.NOTIFICACIONES] ?: true }
    val recibirPedidos: Flow<Boolean> =
        context.dataStore.data.map { it[Claves.RECIBIR_PEDIDOS] ?: true }

    // Escritura: funciones suspend
    suspend fun guardarSesion(rol: String, usuarioId: Int) {
        context.dataStore.edit {
            it[Claves.PERFIL_ACTIVO] = rol
            it[Claves.USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        context.dataStore.edit { it[Claves.MODO_OSCURO] = b }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        context.dataStore.edit { it[Claves.NOTIFICACIONES] = b }
    }

    suspend fun guardarRecibirPedidos(b: Boolean) {
        context.dataStore.edit { it[Claves.RECIBIR_PEDIDOS] = b }
    }

    // Borra solo el perfil activo; modo oscuro y demas ajustes se conservan
    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it[Claves.PERFIL_ACTIVO] = ""
            it[Claves.USUARIO_ID] = 0
        }
    }
}
