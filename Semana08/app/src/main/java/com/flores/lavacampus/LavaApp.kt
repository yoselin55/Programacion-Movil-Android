package com.flores.lavacampus

import android.app.Application
import com.flores.lavacampus.data.local.DatosIniciales
import com.flores.lavacampus.data.local.LavaDatabase
import com.flores.lavacampus.data.prefs.FotoPerfilStore
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.repository.LavaRepository
import com.flores.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LavaApp : Application() {
    val database: LavaDatabase by lazy { LavaDatabase.obtener(this) }
    val prefs: PreferenciasStore by lazy { PreferenciasStore(this) }
    val fotos: FotoPerfilStore by lazy { FotoPerfilStore(this) }

    // Los ViewModels reciben el repository como interface (LavaRepository)
    val repository: LavaRepository by lazy { LavaRepositoryRoom(database) }

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba en segundo plano (solo la primera vez)
        CoroutineScope(Dispatchers.IO).launch { DatosIniciales.sembrar(database) }
    }
}
