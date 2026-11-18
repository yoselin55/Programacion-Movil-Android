package com.flores.campusgps.data

import com.flores.campusgps.model.Posicion

// Commit 1: contrato para pedir la posición; el ViewModel solo conoce esta interfaz.
interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    // Commit 9: el ViewModel pregunta esto antes de buscar, sin necesitar un Context.
    fun playServicesDisponible(): Boolean
}

// Commit 9: se lanza cuando no se puede determinar la ubicación (por ejemplo, con el GPS apagado).
class GpsApagadoException : Exception("Activa el GPS e intenta de nuevo")
