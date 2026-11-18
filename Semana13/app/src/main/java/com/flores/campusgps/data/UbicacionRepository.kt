package com.flores.campusgps.data

import com.flores.campusgps.model.Posicion

// Commit 1: contrato para pedir la posición; el ViewModel solo conoce esta interfaz.
interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
}
