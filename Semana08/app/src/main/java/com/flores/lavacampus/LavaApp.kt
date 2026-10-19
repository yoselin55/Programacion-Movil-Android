package com.flores.lavacampus

import android.app.Application
import com.flores.lavacampus.data.local.DatosIniciales
import com.flores.lavacampus.data.local.LavaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LavaApp : Application() {
    val database: LavaDatabase by lazy { LavaDatabase.obtener(this) }
    // TODO (Paso 4): val repository = LavaRepositoryRoom(database)

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba en segundo plano (solo la primera vez)
        CoroutineScope(Dispatchers.IO).launch { DatosIniciales.sembrar(database) }
    }
}
