package com.flores.campusgps.data

import com.flores.campusgps.model.Posicion

// Mejora 3 (ruta por las calles): contrato para pedir el camino a pie entre dos puntos.
// El ViewModel solo conoce esta interfaz, no la red ni el servicio que calcula la ruta.
interface RutasRepository {
    // Devuelve los puntos del camino, en orden, siguiendo calles y veredas.
    suspend fun obtenerRuta(desde: Posicion, hasta: Posicion): Result<List<Posicion>>
}
