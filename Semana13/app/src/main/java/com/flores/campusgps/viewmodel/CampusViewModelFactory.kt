package com.flores.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flores.campusgps.data.LugaresRepository
import com.flores.campusgps.data.PreferenciasRepository
import com.flores.campusgps.data.RutasRepository
import com.flores.campusgps.data.UbicacionRepository

// Commit 1: fábrica que crea el ViewModel pasándole sus repositorios.
// Mejora 2: ahora también recibe el repositorio de preferencias (DataStore).
// Mejora 3: crea también el ViewModel de la bienvenida.
class CampusViewModelFactory(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val preferenciasRepo: PreferenciasRepository,
    private val rutasRepo: RutasRepository // Mejora 3: camino a pie por las calles
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MapaViewModel::class.java) ->
            MapaViewModel(ubicacionRepo, lugaresRepo, preferenciasRepo, rutasRepo) as T
        modelClass.isAssignableFrom(BienvenidaViewModel::class.java) ->
            BienvenidaViewModel(preferenciasRepo) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
