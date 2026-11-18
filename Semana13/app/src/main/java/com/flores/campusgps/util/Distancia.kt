package com.flores.campusgps.util

import android.location.Location
import com.flores.campusgps.model.Lugar
import com.flores.campusgps.model.Posicion

// Commit 6: función pura que calcula la distancia en metros entre tu posición y un lugar.
fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {
    val resultado = FloatArray(1)
    // El orden es (lat1, lon1, lat2, lon2, resultado); la distancia queda en resultado[0].
    Location.distanceBetween(desde.latitud, desde.longitud, hasta.latitud, hasta.longitud, resultado)
    return resultado[0]
}
