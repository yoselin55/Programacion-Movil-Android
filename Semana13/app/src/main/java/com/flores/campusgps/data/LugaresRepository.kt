package com.flores.campusgps.data

import com.flores.campusgps.model.Lugar

// Commit 1: contrato para obtener los lugares del campus.
interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
